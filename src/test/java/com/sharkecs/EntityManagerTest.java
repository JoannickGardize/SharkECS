package com.sharkecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sharkecs.testutil.SubscriptionLogger;

class EntityManagerTest {

	static class A {
	}

	static class B {
	}

	static class C {
	}

	private Archetype archetype1;
	private Archetype archetype2;
	private Archetype archetype3;

	private ComponentMapper<A> mapperA;
	private ComponentMapper<B> mapperB;
	private ComponentMapper<C> mapperC;

	private Subscription subscriptionA;
	private SubscriptionLogger listenerA;
	private Subscription subscriptionB;
	private SubscriptionLogger listenerB;
	private Subscription subscriptionC;
	private SubscriptionLogger listenerC;

	private Transmutation transmutation;

	private EntityManager manager;

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void initialize() {
		archetype1 = new Archetype("archetype1", 0, A.class, B.class);
		archetype2 = new Archetype("archetype2", 1, C.class);
		archetype3 = new Archetype("archetype3", 2, B.class, C.class);

		mapperA = new FlatArrayComponentMapper<>(1, A::new);
		mapperB = new FlatArrayComponentMapper<>(1, B::new);
		mapperC = new FlatArrayComponentMapper<>(1, C::new);

		subscriptionA = new TrackingSubscription(1);
		listenerA = new SubscriptionLogger();
		subscriptionA.addListener(listenerA);
		subscriptionB = new TrackingSubscription(1);
		listenerB = new SubscriptionLogger();
		subscriptionB.addListener(listenerB);
		subscriptionC = new TrackingSubscription(1);
		listenerC = new SubscriptionLogger();
		subscriptionC.addListener(listenerC);

		archetype1.setComponentMappers(new ComponentMapper[] { mapperA, mapperB });
		archetype1.setAutoCreateComponentMappers(new ComponentMapper[] { mapperA, mapperB });
		archetype2.setComponentMappers(new ComponentMapper[] { mapperC });
		archetype2.setAutoCreateComponentMappers(new ComponentMapper[] { mapperC });
		archetype3.setComponentMappers(new ComponentMapper[] { mapperB, mapperC });
		archetype3.setAutoCreateComponentMappers(new ComponentMapper[] { mapperB, mapperC });

		archetype1.setSubscriptions(new Subscription[] { subscriptionA, subscriptionB });
		archetype2.setSubscriptions(new Subscription[] { subscriptionC });
		archetype3.setSubscriptions(new Subscription[] { subscriptionB, subscriptionC });

		transmutation = new Transmutation(archetype1, archetype3);

		transmutation.setAddMappers(new ComponentMapper[] { mapperC });
		transmutation.setRemoveMappers(new ComponentMapper[] { mapperA });

		transmutation.setAddSubscriptions(new Subscription[] { subscriptionC });
		transmutation.setChangeSubscriptions(new Subscription[] { subscriptionB });
		transmutation.setRemoveSubscriptions(new Subscription[] { subscriptionA });

		archetype1.setTransmutations(new Transmutation[] { null, null, transmutation });

		manager = new EntityManager(10);
	}

	@Test
	void createAndRemoveTest() {

		// Creation test

		List<Integer> managerIdLog = new ArrayList<>();
		managerIdLog.add(manager.create(archetype1));
		managerIdLog.add(manager.create(archetype1));
		managerIdLog.add(manager.create(archetype2));

		Assertions.assertEquals(Arrays.asList(0, 1, 2), managerIdLog);
		Assertions.assertNotNull(mapperA.get(0));
		Assertions.assertNotNull(mapperB.get(0));
		Assertions.assertNull(mapperC.get(0));

		manager.process();

		Assertions.assertEquals(archetype1, manager.archetypeOf(0));
		Assertions.assertEquals(archetype1, manager.archetypeOf(1));
		Assertions.assertEquals(archetype2, manager.archetypeOf(2));
		listenerA.assertAddLog(0, 1);
		listenerA.assertRemoveLog();
		listenerC.assertAddLog(2);
		listenerC.assertRemoveLog();

		clearListeners();

		// Remove & insertion test

		subscriptionA.addListener(new SubscriptionListener() {

			@Override
			public void removed(int entityId) {
				Assertions.assertNotNull(mapperA.get(entityId));
				Assertions.assertNotNull(mapperB.get(entityId));
			}

			@Override
			public void added(int entityId) {
			}

			@Override
			public void changed(int entityId, Transmutation transmutation) {
			}
		});

		manager.remove(1);
		manager.remove(0);
		manager.create(archetype2);
		manager.create(archetype2);

		manager.process();

		listenerA.assertAddLog();
		listenerA.assertRemoveLog(1, 0);
		listenerC.assertAddLog(3, 4);
		listenerC.assertRemoveLog();

		Assertions.assertNull(mapperA.get(0));
		Assertions.assertNull(mapperB.get(0));
		Assertions.assertNotNull(mapperC.get(3));

		// Insertion with recycling test

		Assertions.assertEquals(0, manager.create(archetype1));
		Assertions.assertEquals(1, manager.create(archetype1));
		Assertions.assertEquals(5, manager.create(archetype1));

		manager.process();

		listenerA.assertAddLog(0, 1, 5);

		checkEmptyRun();
	}

	@Test
	void transmuteTest() {
		int id = manager.create(archetype1);

		manager.process();

		clearListeners();

		subscriptionA.addListener(new SubscriptionListener() {

			@Override
			public void removed(int entityId) {
				Assertions.assertNotNull(mapperA.get(entityId));
			}

			@Override
			public void added(int entityId) {
			}

			@Override
			public void changed(int entityId, Transmutation transmutation) {
			}
		});

		manager.transmute(id, archetype3);

		manager.process();

		listenerA.assertAddLog();
		listenerA.assertChangeLog();
		listenerA.assertRemoveLog(0);

		listenerB.assertAddLog();
		listenerB.assertChangeLog(0);
		listenerB.assertTransmutationLog(transmutation);

		listenerB.assertRemoveLog();

		listenerC.assertAddLog(0);
		listenerC.assertChangeLog();
		listenerC.assertRemoveLog();

		Assertions.assertNull(mapperA.get(0));
		Assertions.assertNotNull(mapperB.get(0));
		Assertions.assertNotNull(mapperC.get(0));
		Assertions.assertEquals(archetype3, manager.archetypeOf(0));

		checkEmptyRun();

	}

	private void checkEmptyRun() {
		clearListeners();

		manager.process();

		listenerA.assertAddLog();
		listenerA.assertChangeLog();
		listenerA.assertTransmutationLog();
		listenerA.assertRemoveLog();

		listenerB.assertAddLog();
		listenerB.assertChangeLog();
		listenerB.assertTransmutationLog();
		listenerB.assertRemoveLog();

		listenerC.assertAddLog();
		listenerC.assertChangeLog();
		listenerC.assertTransmutationLog();
		listenerC.assertRemoveLog();
	}

	private void clearListeners() {
		listenerA.clear();
		listenerB.clear();
		listenerC.clear();
	}
}
