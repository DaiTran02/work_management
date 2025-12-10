package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.PersonalRecord;
import ws.core.model.filter.PersonalRecordFilter;
import ws.core.respository.PersonalRepositoryCustom;

@Repository
public class PersonalRepositoryCustomImpl implements PersonalRepositoryCustom{
	
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private Query createCriteria(PersonalRecordFilter personalRecordFilter) {
		List<Criteria> listCriterias = new ArrayList<Criteria>();
		
		if(personalRecordFilter.getUserId() != null) {
			listCriterias.add(Criteria.where("rootUser.idUser").is(personalRecordFilter.getUserId()));
		}
		
		if(personalRecordFilter.getOldUserId() != null) {
			listCriterias.add(Criteria.where("oldUsers.idUser").is(personalRecordFilter.getOldUserId()));
		}
		
		if(personalRecordFilter.getTransferredUserId() != null) {
			listCriterias.add(Criteria.where("usersTransfered").elemMatch(Criteria.where("idUser").is(personalRecordFilter.getTransferredUserId().trim())));
		}
		
	    if(personalRecordFilter.getKeySearch() != null && !personalRecordFilter.getKeySearch().isEmpty()) {
	    	listCriterias.add(Criteria.where("title").is(personalRecordFilter.getKeySearch()));
	    }
		Query query = new Query();
		if(listCriterias.size() > 0) {
			query.addCriteria(new Criteria().andOperator(listCriterias));
		}
		return query;
	}

	@Override
	public List<PersonalRecord> findAll(PersonalRecordFilter filter) {
		Query query = createCriteria(filter);
		
//		query.addCriteria(Criteria.where("rootUser.idUser").is(filter.getUserId()));
//	    if(filter.getKeySearch() != null && !filter.getKeySearch().isEmpty()) {
//	    	query.addCriteria(Criteria.where("title").is(filter.getKeySearch()));
//	    }
		
		return this.mongoTemplate.find(query, PersonalRecord.class);
	}

	@Override
	public PersonalRecord findById(PersonalRecordFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PersonalRecord> findPersonalsByOldUser(PersonalRecordFilter filter) {
	    Query query = createCriteria(filter);
//	    query.addCriteria(Criteria.where("oldUsers.idUser").is(filter.getOldUserId()));
	    
//	    Criteria.where("oldUsers").elemMatch(Criteria.where("idUser").is(filter.getOldUserId().trim()));
	    
//	    if(filter.getKeySearch() != null && !filter.getKeySearch().isEmpty()) {
//	    	query.addCriteria(Criteria.where("title").is(filter.getKeySearch()));
//	    }

	    return mongoTemplate.find(query, PersonalRecord.class);
	}

	@Override
	public List<PersonalRecord> findPersonalsByTransferredUser(PersonalRecordFilter filter) {
		Query query = createCriteria(filter);
//		query.addCriteria(Criteria.where("usersTransfered").elemMatch(Criteria.where("idUser").is(filter.getTransferredUserId().trim())));
//	    if(filter.getKeySearch() != null && !filter.getKeySearch().isEmpty()) {
//	    	query.addCriteria(Criteria.where("title").is(filter.getKeySearch()));
//	    }
	    return mongoTemplate.find(query, PersonalRecord.class);
	}


}
