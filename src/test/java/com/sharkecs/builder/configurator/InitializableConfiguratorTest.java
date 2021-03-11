package com.sharkecs.builder.configurator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Initializable;

class InitializableConfiguratorTest {

	private boolean called;

	@Test
	void configureTest() {
		Initializable initializable = () -> called = true;

		called = false;
		new InitializableConfigurator().prioritizedConfigure(initializable, null);
		Assertions.assertTrue(called);
	}
}
