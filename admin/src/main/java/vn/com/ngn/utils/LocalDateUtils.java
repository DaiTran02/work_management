package vn.com.ngn.utils;


import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

public class LocalDateUtils {
	public static SimpleDateFormat dfDate = new SimpleDateFormat("dd-MM-yyyy");
	public static SimpleDateFormat dfDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static Duration duration;
	
	public static LocalDate longToLocalDate(long longTime) {
		return Instant.ofEpochMilli(longTime).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static LocalDateTime longToLocalDateTime(long longTime) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(longTime), TimeZone.getDefault().toZoneId());
	}
	
	public static Long localDateToLong(LocalDate date) {
		return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static Long localDateTimeToLong(LocalDateTime dateTime) {
		ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
		return zonedDateTime.toInstant().toEpochMilli();
	}
	
	public static LocalDateTime getStartOfTheYear(int year) {
		return LocalDateTime.of(year, 1, 1, 0, 0,0);
	}
	
	public static LocalDateTime getEndtOfTheYear(int year) {
		return LocalDateTime.of(year, 12, 31, 23, 59,59);
	}
	
	public static Locale localeVietNam() {
		return Locale.of("vi","VN");
	}

}
