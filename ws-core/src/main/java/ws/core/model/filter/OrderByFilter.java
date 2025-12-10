package ws.core.model.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class OrderByFilter {
	public enum Direction {
		ASC, DESC;
	}
	
	private HashMap<String, String> orders=new HashMap<>();
	
	public void add(String fieldName, Direction deriction) {
		orders.put(fieldName, deriction.name());
	}
	
	public HashMap<String, String> getOrders(){
		return orders;
	}
	
	public List<Order> getSortBy() {
		List<Order> sortBy=new ArrayList<>();
		if(orders.size()>0) {
			for (Map.Entry<String, String> entry : orders.entrySet()) {
				String field = entry.getKey();
				String direction = entry.getValue();
				if(direction.equalsIgnoreCase("ASC")) {
					sortBy.add(new Order(Sort.Direction.ASC, field));
				}else {
					sortBy.add(new Order(Sort.Direction.DESC, field));
				}
			}
		}else {
			sortBy.add(new Order(Sort.Direction.DESC, "_id"));
		}
		return sortBy;
	}
}
