package org.ibmcloud.demo.cloudconnector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class StringBasedTrustManager implements X509TrustManager {
        private KeyStore ks;
        private X509Certificate cert;
        private X509TrustManager trustManager;
        private static StringBasedTrustManager tm;

        private void setTrustManager() throws IOException, GeneralSecurityException {
          TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
          tmf.init(ks);
          for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
              trustManager = (X509TrustManager) tm;
              break;
            }
          }
          if (trustManager == null) {
            throw new GeneralSecurityException("No X509TrustManager found");
          }
        }
    
        private StringBasedTrustManager() throws IOException, GeneralSecurityException {
          ks = KeyStore.getInstance(KeyStore.getDefaultType());
          // Note: KeyStore requires it be loaded even if you don't load anything into it:
          ks.load(null);
          setTrustManager();
        }

        public void addCert(byte []certbytes) throws IOException, GeneralSecurityException {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream is = new ByteArrayInputStream(certbytes);
            cert = (X509Certificate) cf.generateCertificate(is);
            ks.setCertificateEntry(UUID.randomUUID().toString(), cert);
            setTrustManager();
        }
    
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }
    
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
          trustManager.checkServerTrusted(chain, authType);
        }
    
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[]{cert};
        }

        public static synchronized StringBasedTrustManager getTrustManager()throws IOException, GeneralSecurityException{
            if(tm!=null){return tm;}
            else{tm = new StringBasedTrustManager(); return tm;}
        }
}