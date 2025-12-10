package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Media;
import ws.core.model.filter.MediaFilter;

public interface MediaRepositoryCustom{
	List<Media> findAll(MediaFilter mediaFilter);
	long countAll(MediaFilter mediaFilter);
	Optional<Media> findOne(MediaFilter mediaFilter);
}
