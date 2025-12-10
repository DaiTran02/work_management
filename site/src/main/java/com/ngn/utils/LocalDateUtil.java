package com.ngn.utils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.vaadin.flow.component.datepicker.DatePicker;

public class LocalDateUtil {
	public static SimpleDateFormat dfDate = new SimpleDateFormat("dd-MM-yyyy");
	public static SimpleDateFormat dfDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static Duration duration;
	
	public static LocalDate longToLocalDate(long longTime) {
		return Instant.ofEpochMilli(longTime).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static LocalDateTime longToLocalDateTime(long longTime) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(longTime), TimeZone.getDefault().toZoneId());
	}
	
	public static LocalTime longToLocalTime(long longTime) {
		return Instant.ofEpochMilli(longTime).atZone(ZoneId.systemDefault()).toLocalTime();
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
	
	public static DatePicker.DatePickerI18n i18nVietNam() {
	    DatePicker.DatePickerI18n vietnameseI18n = new DatePicker.DatePickerI18n();
	    
	    vietnameseI18n.setMonthNames(List.of(
	        "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
	        "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8",
	        "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
	    ));

	    vietnameseI18n.setWeekdays(List.of(
	        "Chủ nhật", "Thứ hai", "Thứ ba",
	        "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"
	    ));

	    vietnameseI18n.setWeekdaysShort(List.of(
	        "CN", "T2", "T3", "T4", "T5", "T6", "T7"
	    ));

	    vietnameseI18n.setToday("Hôm nay");
	    vietnameseI18n.setCancel("Hủy");

	    return vietnameseI18n;
	}

}
