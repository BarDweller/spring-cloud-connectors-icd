package org.ozzy.demo.cloudconnector;

import java.util.Map;


import org.springframework.cloud.service.common.RedisServiceInfo;

public class DatabasesForRedisServiceInfoCreator extends DatabasesForCloudServiceInfoCreator<RedisServiceInfo> {

	public DatabasesForRedisServiceInfoCreator() {
		super("databases-for-redis","rediss");
	}

	@Override
	public RedisServiceInfo createServiceInfo(Map<String,Object> serviceData) {
		String id = getId(serviceData);

		Map<String,Object> credentials = getCredentials(serviceData);
        String uri = getUriFromCredentials(credentials);

        return new RedisServiceInfo(id, uri);
	}
}