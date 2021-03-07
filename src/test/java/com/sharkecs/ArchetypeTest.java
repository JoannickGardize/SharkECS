package com.sharkecs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.annotation.AutoCreation;

class ArchetypeTest {

	static class A {

	}

	@AutoCreation(false)
	static class B {

	}

	@Test
	void isAutoCreationTest() {

		Archetype archetype = new Archetype("test", 0, A.class, B.class);

		Assertions.assertFalse(archetype.isAutoCreation(A.class, false));
		Assertions.assertFalse(archetype.isAutoCreation(B.class, true));
		archetype.setAutoCreation(false, A.class);
		Assertions.assertFalse(archetype.isAutoCreation(A.class, true));
		archetype.setAutoCreation(true);
		Assertions.assertTrue(archetype.isAutoCreation(B.class, false));
	}
}
