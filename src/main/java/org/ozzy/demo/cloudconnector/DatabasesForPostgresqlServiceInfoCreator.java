package org.ozzy.demo.cloudconnector;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.Map;


import org.springframework.cloud.service.common.PostgresqlServiceInfo;

public class DatabasesForPostgresqlServiceInfoCreator extends DatabasesForCloudServiceInfoCreator<PostgresqlServiceInfo> {

	public DatabasesForPostgresqlServiceInfoCreator() {
		super("databases-for-postgresql","postgres");
	}

    private final static char[] hexString = "0123456789ABCDEF".toCharArray();
    private void dumpBytes(String label, byte[]data){
        System.out.println(label);
        for(byte b: data){
            int v = b & 0xFF;
            System.out.print(hexString[v >>> 4]);
            System.out.print(hexString[v & 0x0F]);
            System.out.print(" ");
            if(b==0x0a){
                System.out.println("");
            }
        }
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
                    System.out.println("certdata\n"+cert64);
                    System.out.println("decoding cert data");
                    byte []certdata = Base64.getDecoder().decode(cert64);
                    dumpBytes("decoded", certdata);
                    //create dir if doesn't exist yet
                    Files.createDirectories(target.getParent());
                    //write content to root.crt file.
                    Files.write(target, certdata, StandardOpenOption.CREATE);
                    System.out.println("cert created at "+target);
                    System.out.println("cert readback..");
                    byte []readback = Files.readAllBytes(target);
                    dumpBytes("readback", readback);

                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("failed to create cert, "+e.getMessage()+" "+e.getClass().getCanonicalName());
                }
            }else{
                System.out.println("root cert already exists.");
            }
        }else{
            System.out.println("no cert to handle");
        }

        System.out.println("pass, id "+id+" uri "+uri);
        return new PostgresqlServiceInfo(id, uri);
	}
}