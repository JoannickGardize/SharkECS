package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Aspect;
import com.sharkecs.SubscriberAdapter;
import com.sharkecs.Subscription;
import com.sharkecs.TrackingSubscription;
import com.sharkecs.annotation.RequiresEntityTracking;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.builder.EngineBuilder;

class SubscriberConfiguratorTest {

	private List<String> log = new ArrayList<>();

	@WithAll(Integer.class)
	private class A extends SubscriberAdapter {
		@Override
		public void added(int entityId) {
			log.add("A");
		}

	}

	@WithAll(Integer.class)
	@RequiresEntityTracking(false)
	private class B extends SubscriberAdapter {
		@Override
		public void added(int entityId) {
			log.add("B");
		}
	}

	@WithAll(Long.class)
	@RequiresEntityTracking(false)
	private class C extends SubscriberAdapter {

	}

	@Test
	void configureTest() {

		EngineBuilder builder = new EngineBuilder();
		builder.with(new A());
		builder.with(new B());
		builder.with(new C());

		Prioritizer prioritizer = new Prioritizer();
		builder.with(prioritizer);
		builder.before(B.class, A.class);
		prioritizer.configure(builder);

		new SubscriberConfigurator().configure(builder);

		Assertions.assertEquals(2, builder.getRegistrations().typeCount(Subscription.class));
		Subscription s1 = builder.getRegistrations().get(Subscription.class, new Aspect(A.class));
		Subscription s2 = builder.getRegistrations().get(Subscription.class, new Aspect(C.class));
		Assertions.assertNotNull(s1);
		Assertions.assertSame(TrackingSubscription.class, s1.getClass());
		Assertions.assertNotNull(s2);
		Assertions.assertSame(Subscription.class, s2.getClass());
		Assertions.assertNotSame(s1, s2);

		log.clear();
		s1.add(0);
		Assertions.assertEquals(Arrays.asList("B", "A"), log);
	}
}
