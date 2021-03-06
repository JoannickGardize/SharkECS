package com.sharkecs.builder;

public class EngineConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EngineConfigurationException(String message) {
		super(message);
	}

	public EngineConfigurationException(Throwable cause) {
		super(cause);
	}
}
