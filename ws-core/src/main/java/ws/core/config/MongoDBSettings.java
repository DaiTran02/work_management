package ws.core.config;

/*Áp dụng cho cluster*/
//@Configuration
public class MongoDBSettings {

//	@Value("${spring.data.mongodb.uri}")
//	private String mongoUri;
//	
//	@Bean
//	MongoClientSettings mongoClientSettings() {
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> mongoClientSettings <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//		ConnectionString connectionString = new ConnectionString(mongoUri);
//		final MongoClientSettings clientSettings = MongoClientSettings.builder()
//	            .retryWrites(true)
//	            .applyConnectionString(connectionString)
//	            .applyToConnectionPoolSettings((ConnectionPoolSettings.Builder builder) -> {
//	                builder.maxSize(500) // max connections count
//	                        .minSize(200) // min connections count
//	                        .maxConnectionLifeTime(3600, TimeUnit.SECONDS) // max connection life 1 hours
//	                        .maxConnectionIdleTime(3600, TimeUnit.SECONDS) // max connection life 1 hours
//	                        .maxWaitTime(10, TimeUnit.SECONDS);
//	                builder.maintenanceFrequency(5, TimeUnit.SECONDS);
//	                builder.maintenanceInitialDelay(60, TimeUnit.SECONDS);
//	            })
//	            .applyToSocketSettings(builder -> {
//	                builder.connectTimeout(5, TimeUnit.SECONDS);
//	                builder.readTimeout(60, TimeUnit.SECONDS);
//	            })
//	            .applyToClusterSettings(builder -> {
//	            	builder.serverSelectionTimeout(30, TimeUnit.SECONDS);
//	            })
//	            .applicationName("ws-core")
//	            .build();
//	    return clientSettings;
//	}
}
