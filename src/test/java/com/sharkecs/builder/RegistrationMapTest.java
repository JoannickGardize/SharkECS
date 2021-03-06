package com.sharkecs.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RegistrationMapTest {

	@Test
	void putAndGetTest() {
		Integer i1 = 1;
		Integer i2 = 2;
		Integer i3 = 3;
		Long l1 = 1L;

		RegistrationMap map = new RegistrationMap();

		map.put(i1);
		map.put("i2", i2);
		map.put("i3", i3);
		map.put(Number.class, null, i3);
		map.put(l1);

		Assertions.assertSame(i1, map.getOrFail(Integer.class));
		Assertions.assertSame(i2, map.getOrFail(Integer.class, "i2"));
		Assertions.assertSame(i3, map.getOrFail(Integer.class, "i3"));
		Assertions.assertSame(i3, map.getOrFail(Number.class));
		Assertions.assertSame(l1, map.getOrFail(Long.class));
	}

	@Test
	void putFailTest() {
		RegistrationMap map = new RegistrationMap();
		map.put(1);
		Assertions.assertThrows(EngineConfigurationException.class, () -> map.put(2));
	}

	@Test
	void getFailTest() {
		RegistrationMap map = new RegistrationMap();
		map.put(1);
		Assertions.assertThrows(EngineConfigurationException.class, () -> map.getOrFail(Integer.class, "test"));
	}

	void computeIfAbsentTest() {
		RegistrationMap map = new RegistrationMap();

		Integer i = map.computeIfAbsent(Integer.class, null, () -> 3);
		Assertions.assertSame(i, map.computeIfAbsent(Integer.class, null, () -> 3));
		Assertions.assertSame(i, map.getOrFail(Integer.class));
	}
}