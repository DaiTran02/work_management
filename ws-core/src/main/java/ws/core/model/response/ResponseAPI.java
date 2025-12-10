package ws.core.model.response;

import java.io.Serializable;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("serial")
public class ResponseAPI implements Serializable{
	
	private Document document=new Document();
	private HttpStatus status=HttpStatus.OK;
	
	public void setStatus(HttpStatus status) {
		this.status=status;
		this.document.put("status", status.value());
	}
	
	public void setTotal(Object result) {
		this.document.put("total", result);
	}
	
	public void setResult(Object result) {
		this.document.put("result", result);
	}
	
	public void setExpand(Object expand) {
		this.document.put("expand", expand);
	}
	
	public void setMessage(Object message) {
		this.document.put("message", message);
	}
	
	public void setError(Object error) {
		this.document.put("error", error);
	}
	
	public void setOk() {
		this.document.put("status", HttpStatus.OK.value());
		this.document.put("message", "Thành công");
	}
	
	public Object build() {
		return ResponseEntity.status(status).body(document);
	}
	
	public Object buildToCache() {
		return this.document;
	}
}
