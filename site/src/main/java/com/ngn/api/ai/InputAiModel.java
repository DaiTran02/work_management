package com.ngn.api.ai;

import lombok.Data;

@Data
public class InputAiModel {
	private String model;
	private String prompt;
	private boolean raw;
	private boolean stream;
}
