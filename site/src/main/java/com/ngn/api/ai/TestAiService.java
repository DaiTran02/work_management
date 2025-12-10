package com.ngn.api.ai;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.services.CoreExchangeService;

public class TestAiService {
	public static TestModel how(InputAiModel inputAiModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.postOrtherUrl("http://localhost:11434/api/generate", new ParameterizedTypeReference<>() {}, inputAiModel);
	}
}
