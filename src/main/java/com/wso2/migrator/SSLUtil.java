package com.wso2.migrator;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Logger;

public class SSLUtil {
    private static final Logger LOGGER = Logger.getLogger(SSLUtil.class.getName());

    public static void loadTrustStore(String trustStorePath, String trustStorePassword) {
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(trustStorePath)) {
                trustStore.load(fis, trustStorePassword.toCharArray());
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            LOGGER.info("Custom TrustStore Loaded Successfully: " + trustStorePath);
        } catch (Exception e) {
            LOGGER.severe("Failed to load truststore: " + e.getMessage());
        }
    }
}
