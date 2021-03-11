package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.builder.EngineBuilder;

class PrioritizerTest {

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

		prioritizer.configure(builder);

		List<Object> list = new ArrayList<>(Arrays.asList(o1, o2, i, l, s, b, f, d));

		prioritizer.prioritize(list);

		Assertions.assertEquals(Arrays.asList(o2, o1, l, b, s, i, d, f), list);
	}

}
