package ws.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.OrganizationCategory;
import ws.core.model.filter.OrganizationCategoryFilter;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.request.ReqOrganizationCategoryCreate;
import ws.core.model.request.ReqOrganizationCategoryUpdate;
import ws.core.respository.OrganizationCategoryRepository;
import ws.core.respository.OrganizationCategoryRepositoryCustom;
import ws.core.services.OrganizationCategoryService;
import ws.core.services.OrganizationService;

@Service
public class OrganizationCategoryServiceImpl implements OrganizationCategoryService{

	@Autowired
	private OrganizationCategoryRepository organizationCategoryRepository;
	
	@Autowired
	private OrganizationCategoryRepositoryCustom organizationCategoryRepositoryCustom;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Override
	public long countAll(OrganizationCategoryFilter leaderApproveTaskFilter) {
		return organizationCategoryRepositoryCustom.countAll(leaderApproveTaskFilter);
	}

	@Override
	public List<OrganizationCategory> findAll(OrganizationCategoryFilter leaderApproveTaskFilter) {
		return organizationCategoryRepositoryCustom.findAll(leaderApproveTaskFilter);
	}

	@Override
	public Optional<OrganizationCategory> findById(String id) {
		return organizationCategoryRepository.findById(new ObjectId(id));
	}
	
	@Override
	public OrganizationCategory getById(String id) {
		Optional<OrganizationCategory> findOrganizationCategory=findById(id);
		if(findOrganizationCategory.isPresent()) {
			return findOrganizationCategory.get();
		}
		throw new NotFoundElementExceptionAdvice("id ["+id+"] không tồn tại trong hệ thống");
	}

	@Override
	public OrganizationCategory deleteById(String id) {
		OrganizationCategory organizationCategory=getById(id);
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setOrganizationCategoryId(organizationCategory.getId());
		
		if(organizationService.countOrganizationAll(organizationFilter)==0) {
			organizationCategoryRepository.delete(organizationCategory);
			return organizationCategory;
		}
		throw new NotAcceptableExceptionAdvice("Không thể xóa, vì dữ liệu đã được sử dụng");
	}

	@Override
	public OrganizationCategory create(ReqOrganizationCategoryCreate reqOrganizationCategoryCreate) {
		OrganizationCategory organizationCategory=new OrganizationCategory();
		organizationCategory.setName(reqOrganizationCategoryCreate.getName());
		organizationCategory.setDescription(reqOrganizationCategoryCreate.getDescription());
		organizationCategory.setOrder(reqOrganizationCategoryCreate.getOrder());
		organizationCategory.setActive(reqOrganizationCategoryCreate.isActive());
		
		return organizationCategoryRepository.save(organizationCategory);
	}

	@Override
	public OrganizationCategory update(String organizationCategoryId, ReqOrganizationCategoryUpdate reqOrganizationCategoryUpdate) {
		OrganizationCategory organizationCategory=getById(organizationCategoryId);
		organizationCategory.setUpdatedTime(new Date());
		organizationCategory.setName(reqOrganizationCategoryUpdate.getName());
		organizationCategory.setDescription(reqOrganizationCategoryUpdate.getDescription());
		organizationCategory.setOrder(reqOrganizationCategoryUpdate.getOrder());
		organizationCategory.setActive(reqOrganizationCategoryUpdate.isActive());
		
		return organizationCategoryRepository.save(organizationCategory);
	}

}
