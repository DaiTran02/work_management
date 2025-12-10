package com.ngn.api.tasks.actions;

import lombok.Data;

@Data
public class ApiRatingModel {
	private int star;
	private String explain;
	private Double markA;
	private Double markB;
	private Double markC;
	private ApiCreatorActionModel creator;
}
