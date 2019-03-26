package org.ozzy.demo.cloudconnector;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;
import org.springframework.cloud.service.ServiceInfo;

public abstract class DatabasesForCloudServiceInfoCreator<SI extends ServiceInfo> extends CloudFoundryServiceInfoCreator<SI> {

    String label;
    String connectionName;

	public DatabasesForCloudServiceInfoCreator(String label, String connectionName) {
        super(new Tags(label),(String[])null);
		this.label = label;
        this.connectionName = connectionName;
    }

    private String dumpItem(Object o){
        String msg="";
        if(o instanceof Map){
            Map<String, Object> m = (Map<String,Object>)o;
            msg+="{";
            int i=0;
            for(Map.Entry<String,Object> e : m.entrySet()){
                if(i!=0){
                    msg+=",";
                }

                msg+=e.getKey()+":"+dumpItem(e.getValue());
                i++;
            }            
            msg+="}";
        }else if(o instanceof List){
            List l = (List)o;
            msg+="[";
            for(int i=0; i<l.size(); i++){
                if(i!=0){
                    msg+=",";
                }
                msg+=dumpItem(l.get(i));
            }
            msg+="]";
        }else{
            String s = o.toString();
            if(s.length()>16){
                s = s.substring(0, 16);
            }
            msg+="\""+o.toString()+"\"";
        }
        return msg;
    }

	public boolean accept(Map<String, Object> serviceData) {
        System.out.println("DEBUG:"+dumpItem(serviceData));

        if(labelMatches(serviceData)){
            Map<String,Object> creds = getCredentials(serviceData);
            if(creds==null){
                return false;
            }
            if(getId(serviceData)==null){
                return false;
            }
            if(getUriFromCredentials(creds)==null){
                return false;
            }
            return true;
        }
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
                return uris.get(0);
            }
        }
		return null;
    }

	protected String getRootCaFromCredentials(Map<String, Object> credentials) {
        Map<String,Object> connection = (Map<String, Object>) credentials.get("connection");
        if(connection!=null){
            Map<String,Object> details = (Map<String, Object>) connection.get(connectionName);
            if(details!=null){
                Map<String,Object> certinfo = (Map<String,Object>)details.get("certificate");
                return (String)certinfo.get("certificate_base64");
            }
        }
		return null;
    }



}