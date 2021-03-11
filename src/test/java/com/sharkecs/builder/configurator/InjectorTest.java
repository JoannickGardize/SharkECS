package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sharkecs.annotation.Inject;
import com.sharkecs.annotation.SkipInjection;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.builder.EngineConfigurationException;
import com.sharkecs.builder.RegistrationMap;

class InjectorTest {

	private Injector injector;
	private EngineBuilder builder;

	private List<Integer> integerList = new ArrayList<Integer>();
	private List<Long> longList = new ArrayList<>();

	@BeforeEach
	void initialize() {
		builder = EngineBuilder.withDefaults();
		injector = builder.getRegistrations().get(Injector.class);

		RegistrationMap registrations = builder.getRegistrations();

		registrations.put(3);
		registrations.put("fieldName", 2L);
		registrations.put("anotherFieldName", 4L);
		registrations.put(1L);
		registrations.put(List.class, Integer.class, integerList);
		registrations.put(List.class, Long.class, longList);

		builder.autoInjectType(AutoInject.class);
	}

	private static interface AutoInject {
	}

	@SuppressWarnings("unused")
	private static class A implements AutoInject {
		private List<Integer> intList;
		private Long l1;
		private Long fieldName;
		@SkipInjection
		private Long l2;

		public void setIntList(List<Integer> intList) {
			this.intList = intList;
		}

		public void setL1(Long l1) {
			this.l1 = l1;
		}

		public void setFieldName(Long fieldName) {
			this.fieldName = fieldName;
		}

		public void setL2(Long l2) {
			this.l2 = l2;
		}
	}

	@Inject
	@SuppressWarnings("unused")
	private static class B extends D {
		private List<Long> theLongList;

		public void setTheLongList(List<Long> theLongList) {
			this.theLongList = theLongList;
		}

	}

	@Inject(injectParent = true)
	private static class C extends D {
	}

	@Inject
	@SuppressWarnings("unused")
	private static class D {
		private Integer i;

		public void setI(Integer i) {
			this.i = i;
		}

	}

	@SkipInjection
	@SuppressWarnings("unused")
	private static class E implements AutoInject {
		private Integer i;

		public void setI(Integer i) {
			this.i = i;
		}

	}

	@SuppressWarnings("unused")
	private static class F {
		@Inject
		private Long l1;
		private Long l2;
		@SkipInjection
		private Long fieldName;

		public void setL1(Long l1) {
			this.l1 = l1;
		}

		public void setL2(Long l2) {
			this.l2 = l2;
		}

		public void setFieldName(Long fieldName) {
			this.fieldName = fieldName;
		}
	}

	@Inject
	@SuppressWarnings("unused")
	private static class MissingSetter {
		private Long l1;

	}

	@Test
	void testA() {
		A a = new A();
		injector.inject(a, builder.getRegistrations());
		Assertions.assertSame(integerList, a.intList);
		Assertions.assertEquals(1L, a.l1);
		Assertions.assertEquals(2L, a.fieldName);
		Assertions.assertNull(a.l2);
	}

	@Test
	void testB() {
		B b = new B();
		injector.inject(b, builder.getRegistrations());
		Assertions.assertSame(longList, b.theLongList);
		Assertions.assertNull(((D) b).i);
	}

	@Test
	void testC() {
		C c = new C();
		injector.inject(c, builder.getRegistrations());
		Assertions.assertEquals(3, ((D) c).i);
	}

	void testE() {
		E e = new E();
		injector.inject(e, builder.getRegistrations());
		Assertions.assertNull(e.i);
	}

	@Test
	void testF() {
		F f = new F();
		injector.inject(f, builder.getRegistrations());
		Assertions.assertEquals(1L, f.l1);
		Assertions.assertNull(f.l2);
		Assertions.assertNull(f.fieldName);
	}

	@Test
	void testMissingSetter() {
		MissingSetter missingSetter = new MissingSetter();
		RegistrationMap registrations = builder.getRegistrations();
		Assertions.assertThrows(EngineConfigurationException.class, () -> injector.inject(missingSetter, registrations));
	}
}
