package ws.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.Doc;
import ws.core.model.DocHistory;
import ws.core.model.embeded.Creator;
import ws.core.model.filter.DocHistoryFilter;
import ws.core.respository.DocHistoryRepository;
import ws.core.respository.DocHistoryRepositoryCustom;
import ws.core.services.DocHistoryService;

@Service
public class DocHistoryServiceImp implements DocHistoryService {

	@Autowired
	private DocHistoryRepository docHistoryRepository;
	
	@Autowired
	private DocHistoryRepositoryCustom docHistoryRepositoryCustom;

	@Override
	public long countDocHistoryAll(DocHistoryFilter DocHistoryFilter) {
		return docHistoryRepositoryCustom.countAll(DocHistoryFilter);
	}

	@Override
	public List<DocHistory> findDocHistoryAll(DocHistoryFilter DocHistoryFilter) {
		return docHistoryRepositoryCustom.findAll(DocHistoryFilter);
	}

	@Override
	public DocHistory findDocHistoryById(String id) {
		Optional<DocHistory> findDoc=docHistoryRepository.findById(new ObjectId(id));
		if(findDoc.isPresent()) {
			return findDoc.get();
		}
		throw new NotFoundElementExceptionAdvice("docHistoryId ["+id+"] không tồn tại trong hệ thống");
	}

	@Override
	public DocHistory deleteDocHistoryById(String id) {
		DocHistory doc=findDocHistoryById(id);
		docHistoryRepository.delete(doc);
		return doc;
	}

	@Override
	public DocHistory saveDocHistory(Doc doc, String action, Creator creator) {
		DocHistory docHistory=new DocHistory();
		docHistory.setDocId(doc.getId());
		docHistory.setDocData(doc);
		docHistory.setAction(action);
		docHistory.setCreator(creator);
		return docHistoryRepository.save(docHistory);
	}
}
