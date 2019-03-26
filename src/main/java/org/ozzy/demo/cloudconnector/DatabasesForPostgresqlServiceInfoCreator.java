package org.ozzy.demo.cloudconnector;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
            System.out.println("**CHECKING FACTORY");
            if(!uri.contains("sslfactory")){
                System.out.println("- ADDING FACTORY");
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
                System.out.println("- FACTORY ALREADY PRESENT");
            }

            /**
            String homepath = System.getProperty("user.home");
            Path homedir = Paths.get(homepath);
            Path target = homedir.resolve(".postgresql/root.crt");
            //ignore if there's already a root.crt somehow.
            if(!Files.exists(target)){
                try{
                    byte []certdata = Base64.getDecoder().decode(cert64);
                    //create dir if doesn't exist yet
                    Files.createDirectories(target.getParent());
                    //write content to root.crt file.
                    Files.write(target, certdata, StandardOpenOption.CREATE);
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("Failed to create cert, "+e.getMessage()+" "+e.getClass().getCanonicalName());
                }
            }
            */
        }

        System.out.println("Building PostgresqlServiceInfo with id "+id+" and uri "+uri);
        return new PostgresqlServiceInfo(id, uri);
	}
}