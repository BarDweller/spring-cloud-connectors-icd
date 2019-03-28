package org.ibmcloud.demo.cloudconnector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class StringBasedSSLFactory extends SSLSocketFactory {

    protected SSLSocketFactory factory;

    public Socket createSocket(InetAddress host, int port) throws IOException {
      return factory.createSocket(host, port);
    }
  
    public Socket createSocket(String host, int port) throws IOException {
      return factory.createSocket(host, port);
    }
  
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
        throws IOException {
      return factory.createSocket(host, port, localHost, localPort);
    }
  
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
        throws IOException {
      return factory.createSocket(address, port, localAddress, localPort);
    }
  
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
        throws IOException {
      return factory.createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket() throws IOException {
      return factory.createSocket();
    }
  
    public String[] getDefaultCipherSuites() {
      return factory.getDefaultCipherSuites();
    }
  
    public String[] getSupportedCipherSuites() {
      return factory.getSupportedCipherSuites();
    }

    public StringBasedSSLFactory(String arg) throws GeneralSecurityException {
        SSLContext ctx = SSLContext.getInstance("TLS");
        try{
          StringBasedTrustManager tm = StringBasedTrustManager.getTrustManager();
          ctx.init(null, new TrustManager[]{tm}, null);
        }catch(IOException e){
          System.out.println("Failed to obtain trustmanager");
          e.printStackTrace();
        }
        factory = ctx.getSocketFactory();        
    }

}