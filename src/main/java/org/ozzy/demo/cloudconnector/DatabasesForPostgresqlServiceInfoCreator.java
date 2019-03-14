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
        if(cert64!=null){
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
        }
        return new PostgresqlServiceInfo(id, uri);
	}
}