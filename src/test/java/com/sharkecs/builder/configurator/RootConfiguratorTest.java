package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sharkecs.annotation.BeforeAll;
import com.sharkecs.builder.EngineBuilder;

class RootConfiguratorTest {

	private static List<Object> logList = new ArrayList<>();

	private static class LogConfigurator implements Configurator {

		@Override
		public void configure(EngineBuilder engineBuilder) {
			logList.add(getClass());
		}

	}

	private static class B extends LogConfigurator {

	}

	@BeforeAll
	private static class BeforeConfigurator extends LogConfigurator {

	}

	private static class A extends LogConfigurator {

	}

	@BeforeEach
	void initialize() {
		logList.clear();
	}

	@Test
	void configureTest() {
		EngineBuilder builder = new EngineBuilder(1, new RootConfigurator());
		builder.with(new Prioritizer());
		builder.with(new A());
		builder.then(new B());
		builder.with(new BeforeConfigurator());

		builder.build();

		Assertions.assertEquals(Arrays.asList(BeforeConfigurator.class, A.class, B.class), logList);
	}
}
