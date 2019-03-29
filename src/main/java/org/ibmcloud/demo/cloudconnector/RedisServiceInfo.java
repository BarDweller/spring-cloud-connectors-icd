package org.ibmcloud.demo.cloudconnector;

import org.springframework.cloud.service.BaseServiceInfo;

public class RedisServiceInfo extends BaseServiceInfo {
    private byte[] certData;
    private String host;
    private String port;
    private String password;
    private String scheme;


    public RedisServiceInfo(String id, byte[] certData, String host, String port, String password, String scheme ){
        super(id);
        this.certData = certData;
        this.host = host;
        this.port = port;
        this.password = password;
        this.scheme = scheme;
    }

    public byte[] getCertData(){
        return this.certData;
    }

    public String getHost(){
        return this.host;
    }
    public String getPort(){
        return this.port;
    }
    public String getPassword(){
        return this.password;
    }
    public String getScheme(){
        return this.scheme;
    }    
}