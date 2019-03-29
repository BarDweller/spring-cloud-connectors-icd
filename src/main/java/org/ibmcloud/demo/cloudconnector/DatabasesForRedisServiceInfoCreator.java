package org.ibmcloud.demo.cloudconnector;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class DatabasesForRedisServiceInfoCreator extends DatabasesForCloudServiceInfoCreator<RedisServiceInfo> {

	public DatabasesForRedisServiceInfoCreator() {
		super("databases-for-redis","rediss");
	}

	@Override
	public RedisServiceInfo createServiceInfo(Map<String,Object> serviceData) {
		String id = getId(serviceData);

		Map<String,Object> credentials = getCredentials(serviceData);
        List<String> uri = getUrisFromCredentials(credentials);

        String cert64 = getRootCaFromCredentials(credentials);        

        System.out.println("Building RedisServiceInfo with id "+id+" and uri "+uri);
        return new RedisServiceInfo(id, Base64.getDecoder().decode(cert64), getHostFromCredentials(credentials), getPortFromCredentials(credentials), getPasswordFromCredentials(credentials), getSchemeFromCredentials(credentials));
	}
}