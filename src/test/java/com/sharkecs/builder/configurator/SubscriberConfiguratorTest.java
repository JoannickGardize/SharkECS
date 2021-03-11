package com.sharkecs.builder.configurator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Aspect;
import com.sharkecs.SubscriberAdapter;
import com.sharkecs.Subscription;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.builder.EngineBuilder;

class SubscriberConfiguratorTest {

	@WithAll(Integer.class)
	private static class A extends SubscriberAdapter {

	}

	@WithAll(Integer.class)
	private static class B extends SubscriberAdapter {

	}

	@WithAll(Long.class)
	private static class C extends SubscriberAdapter {

	}

	@Test
	void configureTest() {

		EngineBuilder builder = new EngineBuilder();
		builder.with(new A());
		builder.with(new B());
		builder.with(new C());

		new SubscriberConfigurator().configure(builder);

		Assertions.assertEquals(2, builder.getRegistrations().typeCount(Subscription.class));
		Subscription s1 = builder.getRegistrations().get(Subscription.class, new Aspect(A.class));
		Subscription s2 = builder.getRegistrations().get(Subscription.class, new Aspect(C.class));
		Assertions.assertNotNull(s1);
		Assertions.assertNotNull(s2);
		Assertions.assertNotSame(s1, s2);
	}
}
