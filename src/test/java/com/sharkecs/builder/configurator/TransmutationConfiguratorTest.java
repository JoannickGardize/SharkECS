package com.sharkecs.builder.configurator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Archetype;
import com.sharkecs.ComponentMapper;
import com.sharkecs.FlatArrayComponentMapper;
import com.sharkecs.Subscription;
import com.sharkecs.Transmutation;

class TransmutationConfiguratorTest {

	@SuppressWarnings("unchecked")
	@Test
	void configureTest() {

		ComponentMapper<Object> m1 = new FlatArrayComponentMapper<>(0, Object::new);
		ComponentMapper<Object> m2 = new FlatArrayComponentMapper<>(0, Object::new);
		ComponentMapper<Object> m3 = new FlatArrayComponentMapper<>(0, Object::new);
		ComponentMapper<Object> m4 = new FlatArrayComponentMapper<>(0, Object::new);

		Subscription s1 = new Subscription(0);
		Subscription s2 = new Subscription(0);
		Subscription s3 = new Subscription(0);

		Archetype a = new Archetype("a", 0);
		a.setAutoCreateComponentMappers(new ComponentMapper[] { m1, m2 });
		a.setComponentMappers(new ComponentMapper[] { m1, m2 });
		a.setSubscriptions(new Subscription[] { s1, s2 });
		a.setTransmutations(new Transmutation[2]);

		Archetype b = new Archetype("b", 1);
		b.setAutoCreateComponentMappers(new ComponentMapper[] { m2, m3 });
		b.setComponentMappers(new ComponentMapper[] { m2, m3, m4 });
		b.setSubscriptions(new Subscription[] { s2, s3 });

		Transmutation transmutation = new Transmutation(a, b);
		new TransmutationConfigurator().configure(transmutation, null);

		Assertions.assertArrayEquals(transmutation.getAddSubscriptions(), new Subscription[] { s3 });
		Assertions.assertArrayEquals(transmutation.getChangeSubscriptions(), new Subscription[] { s2 });
		Assertions.assertArrayEquals(transmutation.getRemoveSubscriptions(), new Subscription[] { s1 });
		Assertions.assertArrayEquals(transmutation.getAddMappers(), new ComponentMapper[] { m3 });
		Assertions.assertArrayEquals(transmutation.getRemoveMappers(), new ComponentMapper[] { m1 });
		Assertions.assertEquals(transmutation, a.getTransmutations()[1]);
		Assertions.assertNull(a.getTransmutations()[0]);
	}
}
