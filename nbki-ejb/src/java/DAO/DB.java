/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static domain.TimerSessionBean.log;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

@Stateless
public class DB {

    @Resource(lookup = "java:/DSforNBKI")
    private DataSource dataSource;

    public final static String SQL_GET_REQUEST = "{ call ? := BIB_NBKI_GET_REQ() }";
    public final static String SQL_SET_RESPONSE = "{ call ? := BIB_NBKI_SET_RES(?,?) }";
    public final static String SQL_SET_RESPONSE_ERROR = "{ call ? := BIB_NBKI_SET_RES_ERR(?,?) }";
    
    @PostConstruct
    public void initialize() {
        log.log(Level.INFO, "new DB");
    }

    @PreDestroy
    public void deinitialize() {
        log.log(Level.INFO, "destroy DB");
    }
    

    /**
     * exec function in oracle with parameters
     *
     * @param SQL
     * @return ResultSet
     */
    public String ExecFuncPS(String SQL, String[] param) {

        Connection connection = null;
        CallableStatement statement = null;
        String result = null;

        try {

            connection = dataSource.getConnection();

            statement = connection.prepareCall(SQL);

            statement.registerOutParameter(1, Types.VARCHAR);
            for (int i = 0; i < param.length; i++) {
                statement.setString(i + 2, param[i]);
            }

            statement.execute();

            result = statement.getString(1);

        } catch (SQLException e) {
            result = e.getMessage();
        } finally {

            try {

                statement.close();
                connection.close();

            } catch (SQLException ex) {

                log.log(Level.WARNING, "ExecFuncPS " + ex);

            }

            
        }
        return result;
    }

}
