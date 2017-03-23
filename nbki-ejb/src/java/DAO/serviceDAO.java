/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static domain.TimerSessionBean.log;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class serviceDAO {

    @EJB
    private DB db;

    @PostConstruct
    public void initialize() {
        log.log(Level.INFO, "new serviceDAO");
    }

    @PreDestroy
    public void deinitialize() {
        log.log(Level.INFO, "destroy serviceDAO");
    }

    //Получить данные по запросу
    public String getRequest() {
        String[] param = new String[0];
        String res = "";

        res = db.ExecFuncPS(db.SQL_GET_REQUEST, param);

        return res;
    }

    //Передать данные по ответу
    public String setResponse(String id_request, String code) {
        String[] param = new String[2];
        String res = "";
        
        if (!id_request.equals("") & !code.equals("")) {
            param[0] = id_request.trim();
            param[1] = code.trim();
            res = db.ExecFuncPS(db.SQL_SET_RESPONSE, param);
        }
        return res;
    }

    //Передать ошибку по ответу
    public String setResponseError(String id_request, String code) {
        String[] param = new String[2];
        String res = "";
        
        if (!id_request.equals("") & !code.equals("")) {
            param[0] = id_request.trim();
            param[1] = code.trim();
            res = db.ExecFuncPS(db.SQL_SET_RESPONSE_ERROR, param);
        }
        return res;
    }


}
