package ws.core.model.response.data;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;

@SuppressWarnings("serial")
public class TaskCountByStatusModel extends Document{
	
	public TaskCountByStatusModel() {
		put("key", null);
		put("name", null);
		put("shortName", null);
		put("count", 0);
		put("child", Arrays.asList());
	}
	
	public void setKey(String value) {
		this.put("key", value);
	}
	
	public void setName(String value) {
		this.put("name", value);
	}
	
	public void setShortName(String value) {
		this.put("shortName", value);
	}
	
	public void setCount(long value) {
		this.put("count", value);
	}
	
	public void setChild(List<TaskCountByStatusModel> value) {
		this.put("child", value);
	}
	
	public long getCount() {
		return getLong("count");
	}
}
