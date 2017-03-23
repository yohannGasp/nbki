/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.ssl;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;






/**
 *
 * @author evgeniy
 */
public class InsecureTrustManager implements X509TrustManager {
    /**
     * {@inheritDoc}
     */
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
         return null;
    }
}