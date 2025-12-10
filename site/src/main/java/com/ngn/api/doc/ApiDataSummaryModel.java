package com.ngn.api.doc;

import java.util.List;

import lombok.Data;

@Data
public class ApiDataSummaryModel {
    private String key;
    private String name;
    private String shortName;
    private int count;
    private List<ApiDataSummaryModel> child;
}
