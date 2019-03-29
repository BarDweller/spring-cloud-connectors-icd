package org.ibmcloud.demo.cloudconnector;

import static org.springframework.cloud.service.Util.hasClass;

import java.security.GeneralSecurityException;

import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.PooledServiceConnectorConfig;
import org.springframework.cloud.service.ServiceConnectorConfig;
import org.springframework.cloud.service.ServiceConnectorCreationException;
import org.springframework.cloud.service.keyval.RedisConnectionFactoryConfig;
import org.springframework.cloud.service.keyval.RedisJedisClientConfigurer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisSslClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

public class DatabasesForRedisCreator
        extends AbstractServiceConnectorCreator<RedisConnectionFactory, RedisServiceInfo> {

    private static final String JEDIS_CLASS_NAME = "redis.clients.jedis.Jedis";
    private static final String APACHE_COMMONS_POOL_CLASS_NAME = "org.apache.commons.pool2.impl.GenericObjectPoolConfig";

    @Override
    public RedisConnectionFactory create(RedisServiceInfo serviceInfo, ServiceConnectorConfig serviceConnectorConfig) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        configuration.setHostName(serviceInfo.getHost());
        configuration.setPort(Integer.valueOf(serviceInfo.getPort()));
        configuration.setPassword(RedisPassword.of(serviceInfo.getPassword()));

        if (hasClass(JEDIS_CLASS_NAME)) {
            JedisClientConfigurationBuilder builder = JedisClientConfiguration.builder();

            RedisJedisClientConfigurer clientConfigurer = new RedisJedisClientConfigurer();
            if (serviceConnectorConfig instanceof RedisConnectionFactoryConfig) {
                clientConfigurer.configure(builder, (RedisConnectionFactoryConfig) serviceConnectorConfig);
            } else {
                clientConfigurer.configure(builder, (PooledServiceConnectorConfig) serviceConnectorConfig);
            }

            if (connectionIsSecure(serviceInfo)) {
                try{
                    JedisSslClientConfigurationBuilder jedissl = builder.useSsl();
                    byte[] cert = serviceInfo.getCertData();
                    if (cert != null) {
                        try {
                            StringBasedTrustManager.getTrustManager().addCert(cert);
                        } catch (Exception e) {
                            System.out.println("Add of cert failed..");
                            e.printStackTrace();
                        }
                    }
                    jedissl.sslSocketFactory(new StringBasedSSLFactory(""));
                }catch (GeneralSecurityException e){
                    throw new ServiceConnectorCreationException(e);
                }
            }

            JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration, builder.build());
            connectionFactory.afterPropertiesSet();
            return connectionFactory;
        }
		else {
			throw new ServiceConnectorCreationException(String.format("Failed to create cloud Redis " +
					"connection factory for %s service. No client implementation classes " +
					" of Jedis clients implementation (%s) not found",
					serviceInfo.getId(), JEDIS_CLASS_NAME));
		}
	}

	private boolean connectionIsSecure(RedisServiceInfo serviceInfo) {
		return "rediss".equals(serviceInfo.getScheme());
	}
}
