package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Processor;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.testutil.ArrayTestUtils;

class TypeConfiguratorTest {

	static class A implements Processor {

		@Override
		public void process() {
		}

	}

	static class B implements Processor {

		@Override
		public void process() {
		}

	}

	@Test
	void configureTest() {

		List<Object> logList = new ArrayList<Object>();

		TypeConfigurator<Processor> typeConfigurator = new TypeConfigurator<Processor>(Processor.class) {

			@Override
			protected void beginConfiguration(EngineBuilder engineBuilder) {
				logList.add("beginConfiguration");
			}

			@Override
			protected void configure(Processor object, EngineBuilder engineBuilder) {
				logList.add(object);
			}

			@Override
			protected void endConfiguration(EngineBuilder engineBuilder) {
				logList.add("endConfiguration");
			}
		};

		A a = new A();
		B b = new B();

		EngineBuilder builder = new EngineBuilder();
		builder.with(a);
		builder.with(b);
		builder.with(new Object());
		builder.with("test");

		typeConfigurator.configure(builder);

		Assertions.assertEquals(4, logList.size());
		Assertions.assertEquals("beginConfiguration", logList.get(0));
		Assertions.assertEquals("endConfiguration", logList.get(3));
		ArrayTestUtils.assertEqualsAnyOrder(new Object[] { a, b }, logList.get(1), logList.get(2));
	}
}
