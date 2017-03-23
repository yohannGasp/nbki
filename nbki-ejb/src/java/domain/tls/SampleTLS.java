/**
 * $RCSfile$
 * version $Revision: 36939 $
 * created 03.07.2007 13:36:23 by kunina
 * last modified $Date: 2013-07-30 17:59:52 +0400 (Вт., 30 июля 2013) $ by $Author: afevma $
 * (C) ООО Крипто-Про 2004-2007.
 *
 * Программный код, содержащийся в этом файле, предназначен
 * для целей обучения. Может быть скопирован или модифицирован 
 * при условии сохранения абзацев с указанием авторства и прав.
 *
 * Данный код не может быть непосредственно использован
 * для защиты информации. Компания Крипто-Про не несет никакой
 * ответственности за функционирование этого кода.
 */
package domain.tls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Простейший пример использования TLS.
 *
 * @author Copyright 2004-2007 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class SampleTLS {
/**
 *
 */
private SampleTLS() {
}

/**
 * @param args /
 * @throws IOException ошибки ввода-вывода
 */
public static void main(String[] args) throws IOException {

    final int sslPort = 8443;
    try {

        // Обязательные настройки при односторонней и двухсторонней аутентификации: указание, какое хранилище
        // содержит доверенный корневой сертификат, и пароль к хранилищу.
        System.setProperty("javax.net.ssl.trustStoreType", "CertStore");
        System.setProperty("javax.net.ssl.trustStore","/usr/local/cert/icrs.nbki.ru.cer");
        //System.setProperty("javax.net.ssl.trustStorePassword", "");

    } catch (Exception e) {
        Logger.getLogger("LOGGER").log(Level.SEVERE, e.toString());
        //e.printStackTrace();
    } finally {
    }


}
}
