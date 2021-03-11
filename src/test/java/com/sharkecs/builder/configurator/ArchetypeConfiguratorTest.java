package com.sharkecs.builder.configurator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Archetype;
import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.Aspect;
import com.sharkecs.ComponentMapper;
import com.sharkecs.FlatArrayComponentMapper;
import com.sharkecs.Subscription;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.testutil.ArrayTestUtils;

class ArchetypeConfiguratorTest {

	@WithAll(Short.class)
	static class A {

	}

	@WithAll({ Short.class, Integer.class })
	static class B {

	}

	@WithAll(Double.class)
	static class C {

	}

	@Test
	void configureTest() {
		EngineBuilder builder = new EngineBuilder();

		ComponentMapper<Short> shortMapper = new FlatArrayComponentMapper<>(0, () -> (short) 0);
		ComponentMapper<Integer> intMapper = new FlatArrayComponentMapper<>(0, () -> 0);
		ComponentMapper<Long> longMapper = new FlatArrayComponentMapper<>(0, () -> 0L);
		builder.component(Short.class, shortMapper);
		builder.component(Integer.class, intMapper);
		builder.component(Long.class, longMapper);

		Archetype a = builder.archetype("test", Short.class, Integer.class, Long.class);
		a.setComponentCreationPolicy(ComponentCreationPolicy.MANUAL, Short.class);
		builder.archetype("test2", Short.class);

		Subscription sA = new Subscription(0);
		builder.getRegistrations().put(new Aspect(A.class), sA);
		Subscription sB = new Subscription(0);
		builder.getRegistrations().put(new Aspect(B.class), sB);
		Subscription sC = new Subscription(0);
		builder.getRegistrations().put(new Aspect(C.class), sC);

		ArchetypeConfigurator configurator = new ArchetypeConfigurator();
		configurator.setDefaultComponentCreationPolicy(ComponentCreationPolicy.AUTOMATIC);
		configurator.configure(a, builder);

		ArrayTestUtils.assertEqualsAnyOrder(a.getComponentMappers(), shortMapper, intMapper, longMapper);
		ArrayTestUtils.assertEqualsAnyOrder(a.getAutoCreateComponentMappers(), intMapper, longMapper);
		ArrayTestUtils.assertEqualsAnyOrder(a.getSubscriptions(), sA, sB);
		Assertions.assertEquals(2, a.getTransmutations().length);
	}
}
