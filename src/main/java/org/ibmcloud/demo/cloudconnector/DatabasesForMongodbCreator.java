package org.ibmcloud.demo.cloudconnector;

import java.util.List;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.ServiceConnectorConfig;
import org.springframework.cloud.service.ServiceConnectorCreationException;
import org.springframework.cloud.service.document.MongoDbFactoryConfig;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

public class DatabasesForMongodbCreator extends AbstractServiceConnectorCreator<MongoDbFactory, MongodbServiceInfo> {
    @Override
    public MongoDbFactory create(MongodbServiceInfo serviceInfo, ServiceConnectorConfig config){
		try {
            MongoClientOptions.Builder mongoOptionsToUse = getMongoOptions((MongoDbFactoryConfig) config);
            
            byte[] cert = serviceInfo.getCertData();        
            if(cert!=null){
                    try{
                        StringBasedTrustManager.getTrustManager().addCert(cert);
                    }catch(Exception e){
                        System.out.println("Add of cert failed..");
                        e.printStackTrace();
                    }
            }
            mongoOptionsToUse.socketFactory(new StringBasedSSLFactory(""));

            SimpleMongoDbFactory mongoDbFactory = createMongoDbFactory(serviceInfo, mongoOptionsToUse);

            return configure(mongoDbFactory, (MongoDbFactoryConfig) config);
        } catch (UnknownHostException e) {
            throw new ServiceConnectorCreationException(e);
        } catch (MongoException e) {
            throw new ServiceConnectorCreationException(e);
        } catch (GeneralSecurityException e){
            throw new ServiceConnectorCreationException(e);
        }
    }

    private String buildUriString(MongodbServiceInfo serviceInfo){
        List<String> uris = serviceInfo.getUris();
        if(uris.size()==1){
            return uris.get(0);
        }else{
            String uri = "mongodb://";
            int schemelen = uri.length();
            boolean first = true;
            for(String s : uris) {
                if(!first){
                    uri+=",";
                }else{
                    first=false;
                }
                uri+=s.substring(schemelen, s.indexOf("/", schemelen+1));
                System.out.println("DEBUG: interim uri "+uri);
            }
            System.out.println("DEBUG: penultimate uri "+uri);
            uri += uris.get(0).substring(uris.get(0).indexOf("/", schemelen+1));
            System.out.println("DEBUG: final uri "+uri);
            return uri;
        }
    }

	private SimpleMongoDbFactory createMongoDbFactory(MongodbServiceInfo serviceInfo, MongoClientOptions.Builder mongoOptionsToUse) throws UnknownHostException {
		MongoClientURI mongoClientURI = new MongoClientURI(buildUriString(serviceInfo), mongoOptionsToUse);
		MongoClient mongo = new MongoClient(mongoClientURI);
		return new SimpleMongoDbFactory(mongo, mongoClientURI.getDatabase());
	}

	private MongoClientOptions.Builder getMongoOptions(MongoDbFactoryConfig config) {
		MongoClientOptions.Builder builder = MongoClientOptions.builder();
		if (config != null) {
			if (config.getConnectionsPerHost() != null) {
				builder.connectionsPerHost(config.getConnectionsPerHost());
			}
			if (config.getMaxWaitTime() != null) {
				builder.maxWaitTime(config.getMaxWaitTime());
			}
			if (config.getWriteConcern() != null) {
				builder.writeConcern(new WriteConcern(config.getWriteConcern()));
			}
		}
		return builder;
	}

	public SimpleMongoDbFactory configure(SimpleMongoDbFactory mongoDbFactory, MongoDbFactoryConfig config) {
		if (config != null && config.getWriteConcern() != null) {
			WriteConcern writeConcern = WriteConcern.valueOf(config.getWriteConcern());
			if (writeConcern != null) {
				mongoDbFactory.setWriteConcern(writeConcern);
			}
		}
		return mongoDbFactory;
	}    
}