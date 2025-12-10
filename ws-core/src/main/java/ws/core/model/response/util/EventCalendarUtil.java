package ws.core.model.response.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.model.EventCalendar;
import ws.core.services.ConfigurationService;
import ws.core.util.CommonUtil;

@Component
public class EventCalendarUtil {
	
	@Autowired
	private ConfigurationService configurationService;
	
	public Map<String, String> getColors(){
		HashMap<String, String> colors = new HashMap<>();
		colors.put("#854303", "#f6d1af");
		colors.put("#4b0a90", "#debeff");
		colors.put("#009b4b", "#a6ffc6");
		colors.put("#1b4890", "#99d5ef");
		colors.put("#d11f1f", "#ffd5d2");
		colors.put("#19b786", "#e0fff5");
		return colors;
	}
	
	public Document toCommon(EventCalendar eventCalendar) {
		Document document=new Document();
		document.append("id", eventCalendar.getId());
		document.append("createdTime", eventCalendar.getCreatedTime());
		document.append("updatedTime", eventCalendar.getUpdatedTime());
		document.append("type", eventCalendar.getType());
		document.append("period", eventCalendar.getPeriod());
		document.append("from", eventCalendar.getFromLong());
		document.append("to", eventCalendar.getToLong());
		document.append("content", eventCalendar.getContent());
		document.append("notes", eventCalendar.getNotes());
		document.append("color", eventCalendar.getColor());
		document.append("subcolor", getSubColor(eventCalendar.getColor()));
		document.append("hosts", eventCalendar.getHosts());
		document.append("attendeesRequired", eventCalendar.getAttendeesRequired());
		document.append("attendeesNoRequired", eventCalendar.getAttendeesNoRequired());
		document.append("prepareres", eventCalendar.getPrepareres());
		document.append("resources", eventCalendar.getResources());
		document.append("attachments", eventCalendar.getAttachments());
		document.append("creator", eventCalendar.getCreator());
		return document;
	}
	
	public List<EventCalendar> splitEventToDays(EventCalendar eventCalendar, long fromDate, long toDate) {
		List<EventCalendar> results=new ArrayList<EventCalendar>();
		
		// Lấy ngày bắt đầu và kết thúc của Event
        LocalDateTime eventStart = eventCalendar.getFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime eventEnd = eventCalendar.getTo().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
		/* Lấy ngày bắt đầu và kết thúc của Display */
        LocalDateTime displayStart = new Date(fromDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime displayEnd = new Date(toDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
        // Nếu thời gian Event nằm ngoài phạm vi hiển thị, điều chỉnh lại
        if (eventStart.isBefore(displayStart)) {
        	eventStart = displayStart;
        }
        if (eventEnd.isAfter(displayEnd)) {
        	eventEnd = displayEnd;
        }

        // Nếu thời gian bắt đầu lớn hơn kết thúc (do điều chỉnh), trả về danh sách rỗng
        if (eventStart.isAfter(eventEnd)) {
            return results;
        }
		
        // Chạy qua từng ngày trong khoảng thời gian hiển thị
        LocalDate currentDay = eventStart.toLocalDate();
        LocalDate endDay = eventEnd.toLocalDate();
        while (!currentDay.isAfter(endDay)) {
            // Xử lý từng ngày
            LocalDateTime startOfDay = currentDay.atStartOfDay().plusHours(configurationService.getHourWorkStartOfDayEvent()).plusMinutes(configurationService.getMinuteWorkStartOfDayEvent());  // 7h sáng
            LocalDateTime endOfDay = currentDay.atStartOfDay().plusHours(configurationService.getHourWorkEndOfDayEvent()).plusMinutes(configurationService.getMinuteWorkEndOfDayEvent());  // 17h chiều

            // Nếu ngày trong Event nằm trong khoảng hiển thị, tạo Event cho ngày đó
            if (startOfDay.isBefore(eventEnd) && endOfDay.isAfter(eventStart)) {
                // Nếu Event chỉ kéo dài 1 ngày, thời gian bắt đầu và kết thúc phải là thời gian cụ thể trong ngày
                if (currentDay.isEqual(eventStart.toLocalDate()) && currentDay.isEqual(eventEnd.toLocalDate())) {
                	EventCalendar _eventCalendar = CommonUtil.copy(eventCalendar, EventCalendar.class);
					_eventCalendar.setFrom(Date.from(eventStart.atZone(ZoneId.systemDefault()).toInstant()));
                	_eventCalendar.setTo(Date.from(eventEnd.atZone(ZoneId.systemDefault()).toInstant()));
                	results.add(_eventCalendar);
                } else {
                    // Nếu là các ngày trong khoảng thì thời gian bắt đầu là 7h sáng và kết thúc là 17h
                    if (currentDay.isAfter(eventStart.toLocalDate()) && currentDay.isBefore(eventEnd.toLocalDate())) {
                    	EventCalendar _eventCalendar = CommonUtil.copy(eventCalendar, EventCalendar.class);
    					_eventCalendar.setFrom(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
                    	_eventCalendar.setTo(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()));
                    	results.add(_eventCalendar);
                    } else {
                        // Ngày đầu và ngày cuối có thể có thời gian bắt đầu và kết thúc khác
                        if (currentDay.isEqual(eventStart.toLocalDate())) {
                        	EventCalendar _eventCalendar = CommonUtil.copy(eventCalendar, EventCalendar.class);
        					_eventCalendar.setFrom(Date.from(eventStart.atZone(ZoneId.systemDefault()).toInstant()));
                        	_eventCalendar.setTo(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()));
                        	results.add(_eventCalendar);
                        } else if (currentDay.isEqual(eventEnd.toLocalDate())) {
                        	EventCalendar _eventCalendar = CommonUtil.copy(eventCalendar, EventCalendar.class);
        					_eventCalendar.setFrom(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
                        	_eventCalendar.setTo(Date.from(eventEnd.atZone(ZoneId.systemDefault()).toInstant()));
                        	results.add(_eventCalendar);
                        }
                    }
                }
            }
            // Chuyển sang ngày tiếp theo
            currentDay = currentDay.plusDays(1);
        }
		
		return results;
	}
	
	
	private String getSubColor(String color) {
		if(color!=null) {
			String subcolor=getColors().get(color);
			if(subcolor!=null) {
				return subcolor;
			} else if (color.startsWith("#")){
				return color+"9c";
			}
		}
		return null;
	}
	
	public Document toSiteResponse(EventCalendar eventCalendar) {
		return toCommon(eventCalendar);
	}
}
