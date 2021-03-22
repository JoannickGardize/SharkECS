package com.sharkecs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.annotation.CreationPolicy;

class ArchetypeTest {

	static class A {

	}

	@CreationPolicy(ComponentCreationPolicy.MANUAL)
	static class B {

	}

	@Test
	void getComponentCreationPolicySTest() {

		Archetype archetype = new Archetype("test", A.class, B.class);

		Assertions.assertEquals(ComponentCreationPolicy.MANUAL, archetype.getComponentCreationPolicy(A.class, ComponentCreationPolicy.MANUAL));
		Assertions.assertEquals(ComponentCreationPolicy.MANUAL, archetype.getComponentCreationPolicy(B.class, ComponentCreationPolicy.AUTOMATIC));
		archetype.setComponentCreationPolicy(ComponentCreationPolicy.MANUAL, A.class);
		Assertions.assertEquals(ComponentCreationPolicy.MANUAL, archetype.getComponentCreationPolicy(A.class, ComponentCreationPolicy.AUTOMATIC));
		archetype.setComponentCreationPolicy(ComponentCreationPolicy.AUTOMATIC);
		Assertions.assertEquals(ComponentCreationPolicy.AUTOMATIC, archetype.getComponentCreationPolicy(B.class, ComponentCreationPolicy.MANUAL));
	}
}
