package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ws.core.model.User;
import ws.core.model.filter.UserFilter;
import ws.core.respository.UserRepositoryCustom;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private List<Criteria> createCriteria(UserFilter userFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		if(userFilter==null)
			return criteriaList;
		
		if(userFilter.getUserNames()!=null) {
			criteriaList.add(Criteria.where("username").in(userFilter.getUserNames()));
		}
		
		if(userFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"username","email","fullName","jobTitle"};
			for (String key : listKeys) {
				if(userFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(userFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+userFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(userFilter.getIncludeOrganizationIds()!=null) {
			criteriaList.add(Criteria.where("belongOrganizations.organizationId").in(userFilter.getIncludeOrganizationIds()));
		}
		
		if(userFilter.getExcludeOrganizationIds()!=null) {
			criteriaList.add(Criteria.where("belongOrganizations.organizationId").nin(userFilter.getExcludeOrganizationIds()));
		}
		
		if(userFilter.getIncludeUserIds()!=null) {
			criteriaList.add(Criteria.where("_id").in(userFilter.getIncludeUserIds().stream().map(e->new ObjectId(e)).collect(Collectors.toList())));
		}
		
		if(userFilter.getExcludeUserIds()!=null) {
			criteriaList.add(Criteria.where("_id").nin(userFilter.getExcludeUserIds().stream().map(e->new ObjectId(e)).collect(Collectors.toList())));
		}
		
		if(userFilter.getHasUsed()!=null) {
			if(userFilter.getHasUsed().booleanValue()) {
				criteriaList.add(Criteria.where("belongOrganizations.0").exists(true));
			}else {
				criteriaList.add(Criteria.where("belongOrganizations.0").exists(false));
			}
		}
		
		if(userFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(userFilter.getActive().booleanValue()));
		}
		
		if(userFilter.getArchive()!=null) {
			criteriaList.add(Criteria.where("archive").is(userFilter.getArchive().booleanValue()));
		}
		
		if(userFilter.getFirstReviewFilter()!=null) {
			criteriaList.add(Criteria.where("firstReview").exists(true));
			
			if(userFilter.getFirstReviewFilter().getReviewed()!=null) {
				criteriaList.add(Criteria.where("firstReview.reviewed").is(userFilter.getFirstReviewFilter().getReviewed().booleanValue()));
			}
		}
		
		if(userFilter.getProvider()!=null) {
			criteriaList.add(Criteria.where("provider").is(userFilter.getProvider()));
		}
		
		for (Criteria criteria : criteriaList) {
			System.out.println("+ "+criteria.getKey()+": "+criteria.getCriteriaObject().toJson());
		}
		
		return criteriaList;
	}
	
	@Override
	public List<User> findAll(UserFilter userFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(userFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(userFilter!=null && userFilter.getSkipLimitFilter()!=null) {
			query.skip(userFilter.getSkipLimitFilter().getSkip());
			query.limit(userFilter.getSkipLimitFilter().getLimit());
		}
		
		if(userFilter!=null && userFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(userFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, User.class);
	}

	@Override
	public long countAll(UserFilter userFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(userFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, User.class);
	}

	@Override
	public boolean checkPassword(String userId, String password) {
		User user=null;
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("_id").is(new ObjectId(userId)));
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		user = this.mongoTemplate.findOne(query, User.class);
		
		/* Check password */
		if(user!=null && passwordEncoder.matches(password, user.getPassword())) {
			return true;
		}
		return false;
	}

	@Override
	public Optional<User> findOne(UserFilter userFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(userFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(userFilter!=null && userFilter.getSkipLimitFilter()!=null) {
			query.skip(userFilter.getSkipLimitFilter().getSkip());
			query.limit(userFilter.getSkipLimitFilter().getLimit());
		}
		
		if(userFilter!=null && userFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(userFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, User.class));
	}

}
