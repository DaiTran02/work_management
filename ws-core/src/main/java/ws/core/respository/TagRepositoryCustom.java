package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Tag;
import ws.core.model.filter.TagFilter;

public interface TagRepositoryCustom {
	public List<Tag> findAll(TagFilter tagFilter);
	public long countAll(TagFilter tagFilter);
	public Optional<Tag> findOne(TagFilter tagFilter);
}
