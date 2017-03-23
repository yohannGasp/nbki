package domain;

import DAO.serviceDAO;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ru.CryptoPro.ssl.SSLSocketFactoryImpl;

/**
 *
 * @author evgeniy
 */
@Singleton
@Startup
public class TimerSessionBean {

    @EJB
    private serviceDAO serviceDAO;

    public final static Logger log = Logger.getLogger(TimerSessionBean.class.getName());

    private String uri_nbki;
    private String log_path;
    private String log_level;

    /**
     * TimerSessionBean constructor
     *
     */
    public TimerSessionBean() {

        uri_nbki = System.getProperty("uri_nbki");
        log_path = System.getProperty("log_path");
        log_level = System.getProperty("log_level");

        /**
         * Loger
         */
        FileHandler fh;
        try {

            fh = new FileHandler(log_path, true);
            fh.setFormatter(new SimpleFormatter());
            fh.setEncoding("UTF-8");
            log.addHandler(fh);
            log.setLevel(Level.ALL);
            log.log(Level.INFO, "new TimerSessionBean class");

            log.log(Level.INFO, "uri_nbki: " + uri_nbki);
            log.log(Level.INFO, "log_path: " + log_path);
            log.log(Level.INFO, "log_level: " + log_level);

        } catch (IOException e) {

            log.log(Level.WARNING, e.getMessage());

        }

    }

    @PostConstruct
    public void initialize() {
        log.log(Level.INFO, "new TimerSessionBean EJB");
    }

    @PreDestroy
    public void deinitialize() {
        log.log(Level.INFO, "destroy TimerSessionBean");
    }

   @Schedule(dayOfWeek = "Mon-Fri", hour = "9-17", minute = "*/2", second = "0", month = "*", dayOfMonth = "*", year = "*")
    public void myTimer() throws IOException {

        String id_req = "";
        Client client = null;

        log.log(Level.INFO, " ====================================== start ======================================");

        /**
         * from cft
         */
       String res = serviceDAO.getRequest();

        /**
         * if request not null then action else not
         */
        if (res != null && !"".equals(res)) {

            int beg = res.indexOf("<InquiryID>");
            int end = res.indexOf("</InquiryID>");

            if (beg != -1 && end != -1) {
                id_req = res.substring(beg + 11, end);
                log.log(Level.INFO, "id_req: " + id_req);

                /*
                    delete id request                
                 */
                res = res.substring(0, beg);
            }

            if ("debug".equals(log_level)) {
                log.log(Level.INFO, "getRequest(): " + res);
            }

            /**
             * http://icrs.demo.nbki.ru/score
             * http://stackoverflow.com/questions/782178/how-do-i-convert-a-string-to-an-inputstream-in-java
             */
            try (InputStream is = new ByteArrayInputStream(res.getBytes(Charset.forName("cp1251")));) {

                /*
                    send request to nbki
                 */
                Response response = null;
                int code = 0;

                /**
                 *
                 * POST request only binary
                 */

                client = ClientBuilder.newClient();

                try {

                    response = client.
                            target(uri_nbki).
                            request(MediaType.TEXT_XML).
                            post(Entity.entity(is, MediaType.TEXT_XML));

                    code = response.getStatus();
                    log.log(Level.INFO, "HTTP code " + code);

                } catch (Exception ex) {
                    log.log(Level.WARNING, "response " + ex.getMessage());
                }
                /**
                 * answer to cft
                 *
                 */
                if (code == 200) {

                    // String response_1 = response.readEntity(String.class);
                    InputStream response_1 = response.readEntity(InputStream.class);
                    //http://stackoverflow.com/questions/28453056/how-to-read-a-response-from-a-web-service-that-returns-a-byte

                    if ("debug".equals(log_level)) {

                        /*  
                            write response from base64 to file
                         */
                        try (FileOutputStream fos = new FileOutputStream("/tmp/request_" + new Date().getTime() + "_resp.xml.p7s")) {

                            byte[] buffer = new byte[10000];
                            response_1.read(buffer, 0, buffer.length);
                            fos.write(buffer, 0, buffer.length);
                            fos.flush();

                        } catch (IOException ex) {

                            log.log(Level.WARNING, "decoded " + ex.getMessage());

                        }

                    }

                    if (!"".equals(response_1)) {

                        String response_2 = "";

                        /*  
                            read from response file
                         */
                        StringBuilder sb = new StringBuilder();
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(response_1, Charset.forName("cp1251")));
                            String strLine;
                            while ((strLine = br.readLine()) != null) {
                                if (strLine.indexOf("<") != -1) {
                                    sb.append(strLine);
                                }
                            }
                            response_2 = sb.toString();

                        } catch (FileNotFoundException ex) {
                            System.out.println(" " + ex.getMessage());
                        } catch (IOException ex) {
                            System.out.println(" " + ex.getMessage());
                        }

                        beg = response_2.indexOf("<score>");
                        end = response_2.indexOf("</score>");

                        /**
                         * only score_code to cft
                         */
                        if (beg != -1 && end != -1) {

                            String score_code = response_2.substring(beg + 7, end);
                            log.log(Level.INFO, "score_code " + score_code);

                            String res2 = serviceDAO.setResponse(id_req, score_code);
                            log.log(Level.INFO, "setResponse: " + res2);

                            /**
                             * error text
                             */
                        } else {

                            int beg_err = response_2.indexOf("<err>");
                            int end_err = response_2.indexOf("</err>");

                            if (beg_err != -1 && end_err != -1) {

                                String error_text = response_2.substring(beg_err, end_err);

                                int beg_code = error_text.indexOf("<Code>");
                                int end_code = error_text.indexOf("</Code>");

                                if (beg_code != -1 && end_code != -1) {

                                    String error_code = error_text.substring(beg_code + 6, end_code);
                                    log.log(Level.INFO, "error_code " + error_code);

                                    int beg_text = error_text.indexOf("<Text>");
                                    int end_text = error_text.indexOf("</Text>");

                                    if (beg_text != -1 && end_text != -1) {

                                        String error_message = error_text.substring(beg_text + 6, end_text);
                                        log.log(Level.INFO, "error_message " + error_message);

                                        String res3 = serviceDAO.setResponseError(id_req, error_code + " " + error_message);
                                        log.log(Level.INFO, "setResponseError: " + res3);

                                    }
                                }
                            }
                        }
                        /* error text */
                    }
                    /* if (!"".equals(response_1)) { */
                }
                /* if (code == 200) { */

            } catch (IOException ex) {

                log.log(Level.WARNING, "IOException " + ex.getMessage());

            } finally {

                client.close();
                log.log(Level.INFO, "client.close");

            }
        }

        log.log(Level.INFO, " ====================================== end ======================================");

    }

}
