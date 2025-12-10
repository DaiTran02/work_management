package ws.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.validation.Valid;
import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.Tag;
import ws.core.model.filter.TagFilter;
import ws.core.model.request.ReqTagAddClass;
import ws.core.model.request.ReqTagCreate;
import ws.core.model.request.ReqTagEdit;
import ws.core.model.request.ReqTagRemoveClass;
import ws.core.respository.TagRepository;
import ws.core.respository.TagRepositoryCustom;
import ws.core.services.TagService;

@Service
public class TagServiceImpl implements TagService{

	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private TagRepositoryCustom tagRepositoryCustom;
	
	@Override
	public Optional<Tag> findById(String tagId) {
		Optional<Tag> findTag=tagRepository.findById(new ObjectId(tagId));
		if(findTag.isPresent()) {
			return findTag;
		}
		throw new NotFoundElementExceptionAdvice("tagId ["+tagId+"] không tồn tại");
	}

	@Override
	public Tag getById(String tagId) {
		Optional<Tag> findTag = findById(tagId);
		if(findTag.isPresent()) {
			return findTag.get();
		}
		throw new NotFoundElementExceptionAdvice("tagId ["+tagId+"] không tồn tại trong hệ thống");
	}

	@Override
	public List<Tag> findAll(TagFilter tagFilter) {
		return tagRepositoryCustom.findAll(tagFilter);
	}

	@Override
	public Optional<Tag> findOne(TagFilter tagFilter) {
		return tagRepositoryCustom.findOne(tagFilter);
	}

	@Override
	public long countAll(TagFilter tagFilter) {
		return tagRepositoryCustom.countAll(tagFilter);
	}

	@Override
	public Tag create(ReqTagCreate reqTagCreate) {
		Tag tagCreate=new Tag();
		tagCreate.setType(reqTagCreate.getType());
		tagCreate.setName(reqTagCreate.getName());
		tagCreate.setColor(reqTagCreate.getColor());
		tagCreate.setActive(true);
		tagCreate.setArchive(false);
		tagCreate.setClassIds(Arrays.asList());
		tagCreate.setCreator(reqTagCreate.getCreator().toCreator());
		return save(tagCreate);
	}

	@Override
	public Tag update(String tagId, ReqTagEdit reqTagEdit) {
		Tag tagUpdate=getById(tagId);
		tagUpdate.setName(reqTagEdit.getName());
		tagUpdate.setColor(reqTagEdit.getColor());
		tagUpdate.setActive(reqTagEdit.getActive().booleanValue());
		tagUpdate.setClassIds(Arrays.asList());
		return save(tagUpdate);
	}

	@Override
	public Tag delele(String tagId) {
		Tag tagDelete=getById(tagId);
		tagRepository.delete(tagDelete);
		return tagDelete;
	}

	@Override
	public boolean delele(Tag tag) {
		try {
			tagRepository.delete(tag);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Tag save(Tag tag) {
		return tagRepository.save(tag);
	}

	@Override
	public Tag addClass(@Valid ReqTagAddClass reqTagAddClass) {
		Tag tag=getById(reqTagAddClass.getTagId());
		
		if(tag.getType()!=null && !tag.getType().equals(reqTagAddClass.getType())) {
			throw new NotAcceptableExceptionAdvice("Không thể thực hiện, vì type không hợp lệ");
		}
		
		if(tag.getCreator()!=null && !tag.getCreator().getOrganizationId().equals(reqTagAddClass.getCreator().getOrganizationId()) && !tag.getCreator().getOrganizationUserId().equals(reqTagAddClass.getCreator().getOrganizationUserId())) {
			throw new NotAcceptableExceptionAdvice("Không thể thực hiện, vì bạn không phải là owner của tag");
		}
		
		for(String classId:reqTagAddClass.getClassIds()) {
			if(!tag.getClassIds().contains(classId)) {
				tag.getClassIds().add(classId);
			}
		}
		return save(tag);
	}

	@Override
	public Tag removeClass(@Valid ReqTagRemoveClass reqTagRemoveClass) {
		Tag tag=getById(reqTagRemoveClass.getTagId());
		
		if(tag.getType()!=null && !tag.getType().equals(reqTagRemoveClass.getType())) {
			throw new NotAcceptableExceptionAdvice("Không thể thực hiện, vì type không hợp lệ");
		}
		
		if(tag.getCreator()!=null && !tag.getCreator().getOrganizationId().equals(reqTagRemoveClass.getCreator().getOrganizationId()) && !tag.getCreator().getOrganizationUserId().equals(reqTagRemoveClass.getCreator().getOrganizationUserId())) {
			throw new NotAcceptableExceptionAdvice("Không thể thực hiện, vì bạn không phải là owner của tag");
		}
		
		for(String classId:reqTagRemoveClass.getClassIds()) {
			if(tag.getClassIds().contains(classId)) {
				tag.getClassIds().remove(classId);
			}
		}
		return save(tag);
	}

	@Override
	public List<String> getListObjectIdsByTagIds(List<String> tagsId) {
		Assert.notNull(tagsId, "Danh sách tagIds không được null");
		Assert.notEmpty(tagsId, "Danh sách tagIds không được rỗng");
		
		List<String> results=new ArrayList<>();
		TagFilter tagFilter=new TagFilter();
		tagFilter.setIds(tagsId);
		
		List<Tag> tags=findAll(tagFilter);
		for (Tag tag : tags) {
			if(tag.getClassIds().size()>0) {
				results.addAll(tag.getClassIds());
			}
		}
		return results;
	}

}
