package ws.core.services;

import java.util.List;

import ws.core.model.Doc;
import ws.core.model.DocHistory;
import ws.core.model.embeded.Creator;
import ws.core.model.filter.DocHistoryFilter;

public interface DocHistoryService {
	public long countDocHistoryAll(DocHistoryFilter docHistoryFilter);
	
	public List<DocHistory> findDocHistoryAll(DocHistoryFilter docHistoryFilter);
	
	public DocHistory findDocHistoryById(String id);
	
	public DocHistory deleteDocHistoryById(String id);
	
	public DocHistory saveDocHistory(Doc doc, String action, Creator creator);
}
