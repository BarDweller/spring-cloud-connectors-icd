package org.ibmcloud.demo.cloudconnector;

import java.util.List;

import org.springframework.cloud.service.BaseServiceInfo;

public class MongodbServiceInfo extends BaseServiceInfo {
    private byte[] certData;
    private List<String> uriString;

    public MongodbServiceInfo(String id, byte[] certData, List<String> uriString ){
        super(id);
        this.certData = certData;
        this.uriString = uriString;
    }

    public byte[] getCertData(){
        return this.certData;
    }

    public List<String> getUris(){
        return this.uriString;
    }
}