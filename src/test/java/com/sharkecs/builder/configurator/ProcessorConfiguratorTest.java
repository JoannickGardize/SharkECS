package com.sharkecs.builder.configurator;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Engine;
import com.sharkecs.Processor;
import com.sharkecs.builder.EngineBuilder;

class ProcessorConfiguratorTest {

	@Test
	void configureTest() {
		EngineBuilder engineBuilder = new EngineBuilder();
		engineBuilder.with(new Prioritizer());
		engineBuilder.then(new ProcessorConfigurator());
		Processor p = () -> {
		};
		engineBuilder.with(p);
		Engine engine = engineBuilder.build();
		Assertions.assertEquals(Arrays.asList(p), Arrays.asList(engine.getProcessors()));
	}
}
