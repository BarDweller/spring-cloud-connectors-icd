package org.ozzy.demo.cloudconnector;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.ServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.ServiceInfo;

public abstract class DatabasesForCloudServiceInfoCreator<SI extends ServiceInfo> extends CloudFoundryServiceInfoCreator<SI> {

    String label;
    String connectionName;

	public DatabasesForCloudServiceInfoCreator(String label, String connectionName) {
        super(new Tags(label),null);
		this.label = label;
        this.connectionName = connectionName;
    }


	public boolean accept(Map<String, Object> serviceData) {
        if(labelMatches(serviceData)){
            Map<String,Object> creds = getCredentials(serviceData);
            if(creds==null){
                System.out.println("fail, no creds");
                return false;
            }
            if(getId(serviceData)==null){
                System.out.println("fail, no id");
                return false;
            }
            if(getUriFromCredentials(creds)==null){
                System.out.println("fail no uri");
                return false;
            }
            System.out.println("pass");
            return true;
        }
        System.out.println("fail label not matched");
		return false;
	}

	protected boolean labelMatches(Map<String, Object> serviceData) {
		String label = (String) serviceData.get("label");
		return this.label.equals(label);
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getCredentials(Map<String, Object> serviceData) {
		return (Map<String, Object>) serviceData.get("credentials");
    }


	protected String getId(Map<String, Object> serviceData) {
		return (String) serviceData.get("name");
    }

	protected String getUriFromCredentials(Map<String, Object> credentials) {
        Map<String,Object> connection = (Map<String, Object>) credentials.get("connection");
        if(connection!=null){
            Map<String,Object> details = (Map<String, Object>) connection.get(connectionName);
            if(details!=null){
                List<String>uris = (List<String>)details.get("composed");
                System.out.println("pass, uri "+uris.get(0));
                return uris.get(0);
            }else{
                System .out.println("fail, no "+connectionName);
            }
        }else{
            System.out.println("fail, no connection");
        }
		return null;
    }

	protected String getRootCaFromCredentials(Map<String, Object> credentials) {
        Map<String,Object> connection = (Map<String, Object>) credentials.get("connection");
        if(connection!=null){
            Map<String,Object> details = (Map<String, Object>) connection.get(connectionName);
            if(details!=null){
                Map<String,Object> certinfo = (Map<String,Object>)details.get("certificate");
                System.out.println("pass, found cert");
                return (String)certinfo.get("certificate_base64");
            }else{
                System .out.println("fail, no "+connectionName);
            }
        }else{
            System.out.println("fail, no connection");
        }
		return null;
    }



}