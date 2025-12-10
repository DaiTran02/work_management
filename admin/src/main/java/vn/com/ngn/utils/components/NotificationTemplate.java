package vn.com.ngn.utils.components;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotificationTemplate{
	
	public static void success(String content) {
		Notification notification = createNotification(content);
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		notification.open();
	}
	
	public static void error(String content) {
		Notification notification = createNotification(content);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		notification.open();
	}
	
	private static Notification createNotification(String content) {
		Notification notification = new Notification();
		notification.setClassName("notification-position-example");
		notification.setPosition(Position.TOP_CENTER);
		notification.setText(content);
		notification.setDuration(5000);
		
		return notification;
	}

}
