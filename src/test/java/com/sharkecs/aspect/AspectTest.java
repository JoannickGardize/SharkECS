package com.sharkecs.aspect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AspectTest {

	static class A {
	}

	static class B {
	}

	static class C {
	}

	static class D {
	}

	@WithAll({ A.class, B.class })
	static class AnnnotatedWithAll {
	}

	@WithAny({ A.class, B.class })
	static class AnnotatedWithAny {
	}

	@Without({ A.class, B.class })
	static class AnnotatedWithout {
	}

	@WithAll(A.class)
	@WithAny({ B.class, C.class })
	@Without(D.class)
	static class AnnotatedWithMixed {
	}

	@WithAll(A.class)
	@WithAny({ C.class, B.class })
	@Without({ D.class, D.class })
	static class AnnotatedWithMixed2 {
	}

	@Test
	void withAllTest() {
		Aspect aspect = new Aspect(AnnnotatedWithAll.class);

		Set<Class<?>> matching = new HashSet<>(Arrays.asList(A.class, B.class, C.class));
		Assertions.assertTrue(aspect.matches(matching));

		Set<Class<?>> notMatching = new HashSet<>(Arrays.asList(A.class, C.class));
		Assertions.assertFalse(aspect.matches(notMatching));
	}

	@Test
	void withAnyTest() {
		Aspect aspect = new Aspect(AnnotatedWithAny.class);

		Set<Class<?>> matching = new HashSet<>(Arrays.asList(B.class, C.class));
		Assertions.assertTrue(aspect.matches(matching));

		Set<Class<?>> notMatching = new HashSet<>(Arrays.asList(C.class, D.class));
		Assertions.assertFalse(aspect.matches(notMatching));
	}

	@Test
	void withoutTest() {
		Aspect aspect = new Aspect(AnnotatedWithout.class);

		Set<Class<?>> matching = new HashSet<>(Arrays.asList(D.class, C.class));
		Assertions.assertTrue(aspect.matches(matching));

		Set<Class<?>> notMatching = new HashSet<>(Arrays.asList(A.class, C.class));
		Assertions.assertFalse(aspect.matches(notMatching));
	}

	@Test
	void withMixedTest() {
		Aspect aspect = new Aspect(AnnotatedWithMixed.class);

		Set<Class<?>> matching = new HashSet<>(Arrays.asList(A.class, C.class));
		Assertions.assertTrue(aspect.matches(matching));

		Set<Class<?>> notMatching = new HashSet<>(Arrays.asList(C.class));
		Assertions.assertFalse(aspect.matches(notMatching));

		notMatching = new HashSet<>(Arrays.asList(A.class, C.class, D.class));
		Assertions.assertFalse(aspect.matches(notMatching));

		notMatching = new HashSet<>(Arrays.asList(A.class));
		Assertions.assertFalse(aspect.matches(notMatching));
	}

	@Test
	void equalsAndHashcodeTest() {
		Aspect a1 = new Aspect(AnnotatedWithMixed.class);
		Aspect a2 = new Aspect(AnnotatedWithMixed2.class);
		Aspect b = new Aspect(AnnnotatedWithAll.class);

		Assertions.assertEquals(a1, a2);
		Assertions.assertEquals(a1.hashCode(), a2.hashCode());
		Assertions.assertNotEquals(a1, b);
		Assertions.assertEquals(b, b);
	}
}
