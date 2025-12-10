package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import ws.core.model.filter.UpgradeFilter;
import ws.core.respository.UpgradeRepositoryCustom;

@Repository
public class UpgradeRepositoryCustomImpl implements UpgradeRepositoryCustom {

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@SuppressWarnings("unused")
	private List<Criteria> createCriteria(UpgradeFilter upgradeFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		return criteriaList;
	}

}
