package ws.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.advice.ObjectIdExceptionAdvance;
import ws.core.model.EventCalendarFile;
import ws.core.model.filter.EventCalendarFileFilter;
import ws.core.model.request.ReqEventCalendarFileCreate;
import ws.core.model.request.ReqEventCalendarFileUpdate;
import ws.core.respository.EventCalendarFileRepository;
import ws.core.respository.EventCalendarFileRepositoryCustom;
import ws.core.services.EventCalendarFileService;

@Service
public class EventCalendarFileServiceImpl implements EventCalendarFileService{

	@Autowired
	private EventCalendarFileRepository eventCalendarFileRepository;
	
	@Autowired
	private EventCalendarFileRepositoryCustom eventCalendarFileRepositoryCustom;
	
	@Override
	public long countAll(EventCalendarFileFilter eventCalendarFileFilter) {
		return eventCalendarFileRepositoryCustom.countAll(eventCalendarFileFilter);
	}
	
	@Override
	public List<EventCalendarFile> findAll(EventCalendarFileFilter eventCalendarFileFilter){
		return eventCalendarFileRepositoryCustom.findAll(eventCalendarFileFilter);
	}
	
	@Override
	public Optional<EventCalendarFile> findOne(EventCalendarFileFilter eventCalendarFileFilter){
		return eventCalendarFileRepositoryCustom.findOne(eventCalendarFileFilter);
	}
	
	@Override
	public EventCalendarFile getOne(EventCalendarFileFilter eventCalendarFileFilter) {
		Optional<EventCalendarFile> findDoc = findOne(eventCalendarFileFilter);
		if(findDoc.isPresent()) {
			return findDoc.get();
		}
		throw new NotFoundElementExceptionAdvice("eventCalendarFile không tồn tại trong hệ thống");
	}

	public Optional<EventCalendarFile> findById(String id){
		if(ObjectId.isValid(id))
			return eventCalendarFileRepository.findById(new ObjectId(id));
		throw new ObjectIdExceptionAdvance("eventCalendarFile ["+id+"] không hợp lệ");
	}
	
	@Override
	public EventCalendarFile getById(String id) {
		Optional<EventCalendarFile> findDoc = findById(id);
		if(findDoc.isPresent()) {
			return findDoc.get();
		}
		throw new NotFoundElementExceptionAdvice("eventCalendarFile ["+id+"] không tồn tại trong hệ thống");
	}
	
	@Override
	public EventCalendarFile deleteById(String id) {
		EventCalendarFile doc=getById(id);
		eventCalendarFileRepository.delete(doc);
		return doc;
	}
	
	@Override
	public EventCalendarFile create(ReqEventCalendarFileCreate eventCalendarFileCreate) {
		EventCalendarFile eventCalendarFile=new EventCalendarFile();
		eventCalendarFile.setId(ObjectId.get());
		eventCalendarFile.setCreatedTime(new Date());
		eventCalendarFile.setUpdatedTime(new Date());
		eventCalendarFile.setTime(new Date(eventCalendarFileCreate.getTime()));
		eventCalendarFile.setAttachments(eventCalendarFileCreate.getAttachments());
		eventCalendarFile.setCreator(eventCalendarFileCreate.getCreator().toCreator());
		try {
			return eventCalendarFileRepository.save(eventCalendarFile);
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}
	
	@Override
	public EventCalendarFile update(String id, ReqEventCalendarFileUpdate eventCalendarFileUpdate) {
		EventCalendarFile eventCalendarFile=getById(id);
		eventCalendarFile.setUpdatedTime(new Date());
		eventCalendarFile.setTime(new Date(eventCalendarFileUpdate.getTime()));
		eventCalendarFile.setAttachments(eventCalendarFileUpdate.getAttachments());
		return eventCalendarFileRepository.save(eventCalendarFile);
	}
	
	@Override
	public EventCalendarFile save(EventCalendarFile eventCalendarFile) {
		return eventCalendarFileRepository.save(eventCalendarFile);
	}
}
