package com.ngn.tdnv.calendar.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CalendarPeriodEnum {
    DAY("day","Trong ngày"),
    ALLDAY("allday","Cả ngày"),
    DAYS("days", "Nhiều ngày")
    ;

    private String key;
    private String caption;
}
