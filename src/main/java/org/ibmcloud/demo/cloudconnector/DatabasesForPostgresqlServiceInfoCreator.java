package org.ibmcloud.demo.cloudconnector;

import java.util.Base64;
import java.util.Map;

import org.springframework.cloud.service.common.PostgresqlServiceInfo;

public class DatabasesForPostgresqlServiceInfoCreator extends DatabasesForCloudServiceInfoCreator<PostgresqlServiceInfo> {

	public DatabasesForPostgresqlServiceInfoCreator() {
		super("databases-for-postgresql","postgres");
	}

	@Override
	public PostgresqlServiceInfo createServiceInfo(Map<String,Object> serviceData) {
		String id = getId(serviceData);

		Map<String,Object> credentials = getCredentials(serviceData);
        String uri = getUriFromCredentials(credentials);

        String cert64 = getRootCaFromCredentials(credentials);        
        if(cert64!=null && uri.contains("sslmode")){
            if(!uri.contains("sslfactory")){
                try{
                    StringBasedTrustManager.getTrustManager().addCert(Base64.getDecoder().decode(cert64));
                    if(!uri.contains("?")){
                        uri+="?sslfactory="+StringBasedSSLFactory.class.getCanonicalName();
                    }else{
                        uri+="&sslfactory="+StringBasedSSLFactory.class.getCanonicalName();
                    }               
                }catch(Exception e){
                    System.out.println("Add of cert failed..");
                    e.printStackTrace();
                }
            }else{
                System.out.println("sslfactory already present in postgresql, not adding custom handler. Expect truststore issues.");
            }
        }

        System.out.println("Building PostgresqlServiceInfo with id "+id+" and uri "+uri);
        return new PostgresqlServiceInfo(id, uri);
	}
}