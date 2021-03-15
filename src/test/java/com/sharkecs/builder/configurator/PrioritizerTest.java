package com.sharkecs.builder.configurator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.builder.EngineConfigurationException;

class PrioritizerTest {

	@Retention(RetentionPolicy.RUNTIME)
	private static @interface AnAnnotation {

	}

	@Retention(RetentionPolicy.RUNTIME)
	private static @interface AnotherAnnotation {

	}

	@AnAnnotation
	private static class Annotated {

	}

	@AnotherAnnotation
	private static class AnotherAnnotated {

	}

	@Test
	void configureTest() {
		Prioritizer prioritizer = new Prioritizer();

		EngineBuilder builder = new EngineBuilder();
		Object o1 = new Object();
		Object o2 = new Object();
		Object marker = new Object();
		Integer i = 3;
		Long l = 2L;
		Short s = (short) 1;
		Byte b = (byte) 4;
		Double d = 3.2;
		Float f = 3.1f;

		builder.with(prioritizer);

		builder.with("t", o2);
		builder.then("t2", o1);
		builder.with(i);
		builder.with(l);
		builder.with(s);
		builder.with(b);
		builder.with(d);
		builder.with(f);

		builder.after(marker, o1, o2);
		builder.after(Long.class, marker);
		builder.after(Byte.class, Long.class);
		builder.after(s, Byte.class);
		builder.before(Short.class, Integer.class);
		builder.before(i, Double.class);
		builder.before(d, Float.class);
		builder.before(Byte.class, Double.class);

		List<Object> list = new ArrayList<>(Arrays.asList(o1, o2, i, l, s, b, f, d));

		Assertions.assertThrows(EngineConfigurationException.class, () -> prioritizer.prioritize(list));

		prioritizer.configure(builder);

		prioritizer.prioritize(list);

		Assertions.assertThrows(EngineConfigurationException.class, () -> builder.after(s, Byte.class));
		Assertions.assertThrows(EngineConfigurationException.class, () -> builder.before(Short.class, Integer.class));

		Assertions.assertEquals(Arrays.asList(o2, o1, l, b, s, i, d, f), list);
	}

	@Test
	void configureWithAnnotationTest() {

		Prioritizer prioritizer = new Prioritizer();
		EngineBuilder builder = new EngineBuilder();

		Object o1 = new Object();
		Object o2 = new Object();
		Object annotated = new Annotated();
		Object annotated2 = new Annotated();
		Object anotherAnnotated = new AnotherAnnotated();

		builder.with(prioritizer);

		builder.with("a1", annotated2);
		builder.with("a2", annotated);
		builder.with("o1", o1);
		builder.with(anotherAnnotated);
		builder.with("o2", o2);

		builder.before(o1, AnAnnotation.class);
		builder.after(AnotherAnnotation.class, AnAnnotation.class);
		builder.after(annotated2, annotated);
		builder.after(o2, AnotherAnnotation.class);

		prioritizer.configure(builder);

		List<Object> list = new ArrayList<>(Arrays.asList(annotated2, o1, o2, annotated, anotherAnnotated));

		prioritizer.prioritize(list);

		Assertions.assertEquals(Arrays.asList(o1, annotated, annotated2, anotherAnnotated, o2), list);
	}

}
