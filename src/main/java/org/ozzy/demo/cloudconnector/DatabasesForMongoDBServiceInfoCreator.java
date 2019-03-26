package org.ozzy.demo.cloudconnector;

import java.util.Map;

import org.springframework.cloud.service.common.MongoServiceInfo;

public class DatabasesForMongoDBServiceInfoCreator extends DatabasesForCloudServiceInfoCreator<MongoServiceInfo> {

	public DatabasesForMongoDBServiceInfoCreator() {
		super("databases-for-mongodb","mongodb");
	}

	@Override
	public MongoServiceInfo createServiceInfo(Map<String,Object> serviceData) {
		String id = getId(serviceData);

		Map<String,Object> credentials = getCredentials(serviceData);
        String uri = getUriFromCredentials(credentials);

        return new MongoServiceInfo(id, uri);
	}
}