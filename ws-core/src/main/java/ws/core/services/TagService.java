package ws.core.services;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import ws.core.model.Tag;
import ws.core.model.filter.TagFilter;
import ws.core.model.request.ReqTagAddClass;
import ws.core.model.request.ReqTagCreate;
import ws.core.model.request.ReqTagEdit;
import ws.core.model.request.ReqTagRemoveClass;

public interface TagService {
	
	public Optional<Tag> findById(String tagId);
	
	public Tag getById(String tagId);
	
	public List<Tag> findAll(TagFilter tagFilter);
	
	public Optional<Tag> findOne(TagFilter tagFilter);
	
	public long countAll(TagFilter tagFilter);
	
	public Tag create(ReqTagCreate reqTagCreate);
	
	public Tag update(String tagId, ReqTagEdit reqTagEdit);
	
	public Tag delele(String tagId);
	
	public boolean delele(Tag tag);
	
	public Tag save(Tag tag);

	public Tag addClass(@Valid ReqTagAddClass reqTagAddClass);

	public Tag removeClass(@Valid ReqTagRemoveClass reqTagRemoveClass);
	
	public List<String> getListObjectIdsByTagIds(List<String> tagsId);
}