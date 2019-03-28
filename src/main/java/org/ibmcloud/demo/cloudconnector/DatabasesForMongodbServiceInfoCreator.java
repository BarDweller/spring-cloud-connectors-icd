package org.ibmcloud.demo.cloudconnector;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class DatabasesForMongodbServiceInfoCreator extends DatabasesForCloudServiceInfoCreator<MongodbServiceInfo> {

	public DatabasesForMongodbServiceInfoCreator() {
		super("databases-for-mongodb","mongodb");
	}

	@Override
	public MongodbServiceInfo createServiceInfo(Map<String,Object> serviceData) {
		String id = getId(serviceData);

		Map<String,Object> credentials = getCredentials(serviceData);
        List<String> uri = getUrisFromCredentials(credentials);

        String cert64 = getRootCaFromCredentials(credentials);        

        System.out.println("Building MongodbServiceInfo with id "+id+" and uri "+uri);
        return new MongodbServiceInfo(id, Base64.getDecoder().decode(cert64), uri);
	}
}