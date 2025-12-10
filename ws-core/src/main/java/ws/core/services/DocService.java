package ws.core.services;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import ws.core.enums.DocAction;
import ws.core.model.Doc;
import ws.core.model.filter.DocFilter;
import ws.core.model.request.ReqDocConfirmComplete;
import ws.core.model.request.ReqDocCreate;
import ws.core.model.request.ReqDocCreatePartner;
import ws.core.model.request.ReqDocUpdate;
import ws.core.model.request.ReqDocUpdatePartner;

public interface DocService {
	public long countDocAll(DocFilter docFilter);
	
	public List<Doc> findDocAll(DocFilter docFilter);
	
	public Optional<Doc> findDocById(String id);
	
	public Doc getDocById(String id);
	
	public Doc deleteDocById(String id);
	
	public Doc createDoc(ReqDocCreate reqDocCreate);
	
	public Doc updateDoc(String docId, ReqDocUpdate reqDocUpdate);
	
	public Doc saveDoc(Doc doc, DocAction docAction);
	
	public Optional<Doc> findDocByIOfficeId(String iofficeId);
	
	public Doc getDocByIOfficeId(String iofficeId);
	
	public Doc createDoc(ReqDocCreatePartner reqDocCreatePartner);
	
	public Doc updateDoc(String docId, ReqDocUpdatePartner reqDocUpdatePartner);

	public long getCountTask(String docId);
	
	public long syncCountTask(String docId);
	
	/**
	 * Kiểm tra xem Người dùng A trong Đơn vị B có liên quan bất kỳ văn bản nào không?
	 * @param organizationId
	 * @param userId
	 * @return
	 */
	public boolean isReferenceAnyDoc(String organizationId, String userId);

	public Doc confirmComplete(String id, @Valid ReqDocConfirmComplete reqDocConfirmComplete);
}
