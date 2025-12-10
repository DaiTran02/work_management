package ws.core.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

//@Configuration
//@DependsOn("mongoTemplate")
public class MongoDBConfigIndex {
	@Autowired
    protected MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
		/* AppMobi */
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("createdTime", Sort.Direction.DESC).named("createdTime"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("userId", Sort.Direction.DESC).named("userId"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("deviceId", Sort.Direction.DESC).named("deviceId"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("username", Sort.Direction.DESC).named("username"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("longitute", Sort.Direction.DESC).named("longitute"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("lagitute", Sort.Direction.DESC).named("lagitute"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("active", Sort.Direction.DESC).named("active"));
//        
//		/* Doc */
//        mongoTemplate.indexOps(Doc.class).ensureIndex(new Index()
//        		.on("docCategory", Sort.Direction.DESC)
//        		.on("docNumber", Sort.Direction.DESC)
//        		.on("docSymbol", Sort.Direction.DESC)
//        		.on("docRegDate", Sort.Direction.DESC)
//        		.on("idPackage", Sort.Direction.DESC)
//        		.on("idIOffice", Sort.Direction.DESC)
//        		.named("doc_unique").unique());
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("createdTime", Sort.Direction.DESC).named("createdTime"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docCategory", Sort.Direction.DESC).named("docCategory"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docSecurity", Sort.Direction.DESC).named("docSecurity"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docNumber", Sort.Direction.DESC).named("docNumber"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docSymbol", Sort.Direction.DESC).named("docSymbol"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docSignal", Sort.Direction.DESC).named("docSignal"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docDate", Sort.Direction.DESC).named("docDate"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docRegDate", Sort.Direction.DESC).named("docRegDate"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docType", Sort.Direction.DESC).named("docType"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docSigner", Sort.Direction.DESC).named("docSigner"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docSummary", Sort.Direction.DESC).named("docSummary"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docAttachments", Sort.Direction.DESC).named("docAttachments"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("countTask", Sort.Direction.DESC).named("countTask"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("active", Sort.Direction.DESC).named("active"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docCreator", Sort.Direction.DESC).named("docCreator"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("docGroup", Sort.Direction.DESC).named("docGroup"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("idPackage", Sort.Direction.DESC).named("idPackage"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("idIOffice", Sort.Direction.DESC).named("idIOffice"));
//        mongoTemplate.indexOps(AppMobi.class).ensureIndex(new Index().on("idVanbanMysql", Sort.Direction.DESC).named("idVanbanMysql"));
        
        /*System.out.println("Mongo DB indexed:");
        int count=1;
        for(String collectionName:mongoTemplate.getCollectionNames()) {
        	System.out.println(count++ +". Collection name: ["+collectionName+"]");
        	List<IndexInfo> indexInfos=mongoTemplate.indexOps(collectionName).getIndexInfo();
        	int fetch=1;
        	for (IndexInfo indexInfo : indexInfos) {
				System.out.println(count+"."+fetch++ +". "+indexInfo.getName());
			}
        	System.out.println();
        }*/
    }
}
