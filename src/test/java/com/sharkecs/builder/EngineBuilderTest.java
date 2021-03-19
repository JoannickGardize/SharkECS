package com.sharkecs.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Archetype;
import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.ComponentMapper;
import com.sharkecs.Engine;
import com.sharkecs.EntityManager;
import com.sharkecs.EntityReference;
import com.sharkecs.IteratingSystem;
import com.sharkecs.Subscription;
import com.sharkecs.Transmutation;
import com.sharkecs.annotation.CreationPolicy;
import com.sharkecs.annotation.SkipInject;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.annotation.WithAny;
import com.sharkecs.testutil.ArrayTestUtils;

// TODO reduce this class to the minimum test cases and make assembly test of the default configuration in the package com.sharkecs.example
class EngineBuilderTest {

	@WithAny({ A.class, B.class })
	public static class FakeSystemA extends IteratingSystem {

		private Object thing;
		private Object somethingElse;

		private ComponentMapper<A> mapperA;
		private ComponentMapper<B> mapperB;
		private ComponentMapper<C> mapperC;

		private EntityManager entityManager;

		@SkipInject
		private List<Integer> entityLog = new ArrayList<>();

		@Override
		public void process(int entityId) {
			entityLog.add(entityId);
		}

		public Object getThing() {
			return thing;
		}

		public void setThing(Object thing) {
			this.thing = thing;
		}

		public Object getSomethingElse() {
			return somethingElse;
		}

		public void setSomethingElse(Object somethingElse) {
			this.somethingElse = somethingElse;
		}

		public ComponentMapper<A> getMapperA() {
			return mapperA;
		}

		public void setMapperA(ComponentMapper<A> mapperA) {
			this.mapperA = mapperA;
		}

		public ComponentMapper<B> getMapperB() {
			return mapperB;
		}

		public void setMapperB(ComponentMapper<B> mapperB) {
			this.mapperB = mapperB;
		}

		public ComponentMapper<C> getMapperC() {
			return mapperC;
		}

		public void setMapperC(ComponentMapper<C> mapperC) {
			this.mapperC = mapperC;
		}

		public EntityManager getEntityManager() {
			return entityManager;
		}

		@Override
		public void setEntityManager(EntityManager entityManager) {
			this.entityManager = entityManager;
		}

		public List<Integer> getEntityLog() {
			return entityLog;
		}
	}

	@WithAll(C.class)
	public static class FakeSystemB extends IteratingSystem {

		private Archetype archetypeA;
		private Archetype archetypeB;

		@Override
		public void process(int entityId) {

		}

		public Archetype getArchetypeA() {
			return archetypeA;
		}

		public void setArchetypeA(Archetype archetypeA) {
			this.archetypeA = archetypeA;
		}

		public Archetype getArchetypeB() {
			return archetypeB;
		}

		public void setArchetypeB(Archetype archetypeB) {
			this.archetypeB = archetypeB;
		}
	}

	static class A {
		EntityReference reference;
	}

	static class B {

	}

	@CreationPolicy(ComponentCreationPolicy.MANUAL)
	static class C {

	}

	@Test
	void failConstructorTest() {
		Assertions.assertThrows(EngineConfigurationException.class, () -> new EngineBuilder(0));
	}

	@Test
	void defaultBuildTest() {
		EngineBuilder builder = EngineBuilder.withDefaults();
		builder.defaultComponentCreationPolicy(ComponentCreationPolicy.AUTOMATIC);

		builder.component(A.class, A::new);
		builder.component(B.class, B::new);
		builder.component(C.class, C::new);

		Archetype archetypeA = builder.archetype("archetypeA", A.class);
		Archetype archetypeB = builder.archetype("archetypeB", B.class, C.class);
		Archetype archetypeC = builder.archetype("archetypeC", C.class);

		builder.transmutation("archetypeA", "archetypeB");

		FakeSystemA systemA = new FakeSystemA();
		FakeSystemB systemB = new FakeSystemB();
		builder.with(systemA);
		builder.then(systemB);

		Object something = new Object();
		Object somethingElse = new Object();
		builder.with(something);
		builder.with("somethingElse", somethingElse);

		Engine engine = builder.build();

		// processors assertions
		Assertions.assertEquals(3, engine.getProcessors().length);
		Assertions.assertEquals(EntityManager.class, engine.getProcessors()[0].getClass());
		Assertions.assertSame(systemA, engine.getProcessors()[1]);
		Assertions.assertSame(systemB, engine.getProcessors()[2]);

		// archetypeA assertions
		ArrayTestUtils.assertEqualsAnyOrder(archetypeA.getSubscriptions(), systemA.getSubscription());

		ArrayTestUtils.assertEqualsAnyOrder(archetypeA.getComponentMappers(), systemA.getMapperA());
		ArrayTestUtils.assertEqualsAnyOrder(archetypeA.getAutoCreateComponentMappers(), systemA.getMapperA());

		Assertions.assertEquals(3, archetypeA.getTransmutations().length);
		Assertions.assertNull(archetypeA.getTransmutations()[0]);
		Assertions.assertEquals(archetypeA, archetypeA.getTransmutations()[1].getFrom());
		Assertions.assertEquals(archetypeB, archetypeA.getTransmutations()[1].getTo());
		Assertions.assertNull(archetypeA.getTransmutations()[2]);

		// archetypeB assertions
		ArrayTestUtils.assertEqualsAnyOrder(archetypeB.getSubscriptions(), systemA.getSubscription(), systemB.getSubscription());

		ArrayTestUtils.assertEqualsAnyOrder(archetypeB.getComponentMappers(), systemA.getMapperB(), systemA.getMapperC());
		ArrayTestUtils.assertEqualsAnyOrder(archetypeB.getAutoCreateComponentMappers(), systemA.getMapperB());

		ArrayTestUtils.assertEqualsAnyOrder(archetypeB.getTransmutations(), null, null, null);

		// transmutation assertions

		Transmutation transmutation = archetypeA.getTransmutations()[1];

		ArrayTestUtils.assertEqualsAnyOrder(archetypeB.getAutoCreateComponentMappers(), (Object[]) transmutation.getAddMappers());
		ArrayTestUtils.assertEqualsAnyOrder(archetypeA.getComponentMappers(), (Object[]) transmutation.getRemoveMappers());
		ArrayTestUtils.assertEqualsAnyOrder(new Subscription[] { systemB.getSubscription() }, (Object[]) transmutation.getAddSubscriptions());
		Assertions.assertEquals(0, transmutation.getRemoveSubscriptions().length);
		Assertions.assertArrayEquals(new Subscription[] { systemA.getSubscription() }, transmutation.getChangeSubscriptions());

		// FakeSystemA assertions
		Assertions.assertSame(something, systemA.getThing());
		Assertions.assertSame(somethingElse, systemA.getSomethingElse());

		// FakeSystemB assertions
		Assertions.assertSame(archetypeA, systemB.getArchetypeA());
		Assertions.assertSame(archetypeB, systemB.getArchetypeB());

		// Iteration test

		EntityManager entityManager = systemA.getEntityManager();

		int id = entityManager.create(archetypeC);
		Assertions.assertNull(systemA.getMapperC().get(id));
		id = entityManager.create(archetypeA);
		Assertions.assertNotNull(systemA.getMapperA().get(id));
		entityManager.create(archetypeA);

		engine.process();

		Assertions.assertEquals(Arrays.asList(1, 2), systemA.getEntityLog());
	}

	private int testID;

	@Test
	@SuppressWarnings("unchecked")
	void referenceTest() {
		EngineBuilder builder = EngineBuilder.withDefaults();

		builder.component(A.class, A::new);

		ComponentMapper<A> mapper = builder.getRegistrations().get(ComponentMapper.class, A.class);
		EntityManager manager = builder.getRegistrations().get(EntityManager.class);

		Archetype archetypeA = builder.archetype("archetypeA", A.class);

		Engine engine = builder.build();

		int id1 = manager.create(archetypeA);
		int id2 = manager.create(archetypeA);
		int id3 = manager.create(archetypeA);

		A a1 = mapper.create(id1);
		a1.reference = manager.reference(id2);

		mapper.create(id2);

		engine.process();

		A a3 = mapper.create(id3);
		a3.reference = manager.reference(id2);

		Assertions.assertEquals(id2, a1.reference.get());
		Assertions.assertTrue(a1.reference.exists());
		Assertions.assertEquals(id2, a3.reference.get());
		a3.reference.ifExists(i -> testID = i);
		Assertions.assertEquals(id2, testID);

		manager.remove(id2);

		engine.process();

		Assertions.assertEquals(-1, a1.reference.get());
		Assertions.assertFalse(a1.reference.exists());
		Assertions.assertEquals(-1, a3.reference.get());
		a3.reference.ifExists(i -> Assertions.fail("should not be executed"));

	}
}
