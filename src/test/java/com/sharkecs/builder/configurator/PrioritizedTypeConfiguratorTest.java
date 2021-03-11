package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Processor;
import com.sharkecs.builder.EngineBuilder;

class PrioritizedTypeConfiguratorTest {

	private static class A implements Processor {

		@Override
		public void process() {
		}
	}

	private static class B implements Processor {

		@Override
		public void process() {
		}

	}

	@Test
	void configureTest() {
		EngineBuilder builder = new EngineBuilder();
		builder.with(new Prioritizer());
		A a = new A();
		B b = new B();
		builder.with(new Object());
		builder.with(a);
		builder.then(b);
		builder.with(2);

		List<Object> logList = new ArrayList<>();

		builder.getRegistrations().get(Prioritizer.class).configure(builder);
		new PrioritizedTypeConfigurator<Processor>(Processor.class) {

			@Override
			protected void prioritizedConfigure(Processor object, EngineBuilder engineBuilder) {
				logList.add(object);
			}

		}.configure(builder);

		Assertions.assertArrayEquals(new Object[] { a, b }, logList.toArray());
	}
}
