/*
 * Copyright 2024 Joannick Gardize
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.sharkecs.builder;

import com.sharkecs.Archetype;
import com.sharkecs.Transmutation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class ArchetypeVariantsBuilderTest {

    private static class A {

    }

    private static class B {

    }

    private static class C {

    }

    private static class D {

    }

    private static class E {

    }

    @Test
    void test() {
        ArchetypeVariantsBuilder builder = new ArchetypeVariantsBuilder("base", A.class, B.class);
        builder.variant("C", C.class);
        builder.variant("D", D.class);
        builder.variant("E", E.class);

        EngineBuilder engineBuilder = EngineBuilder.withDefaults();
        builder.apply(engineBuilder);

        RegistrationMap registrations = engineBuilder.getRegistrations();

        Set<Class<?>> expected = new HashSet<>();

        Assertions.assertEquals(8, registrations.typeCount(Archetype.class));
        Assertions.assertEquals(56, registrations.typeCount(Transmutation.class));
        assertArchetype(registrations, expected, "base", A.class, B.class);
        assertArchetype(registrations, expected, "baseC", A.class, B.class, C.class);
        assertArchetype(registrations, expected, "baseD", A.class, B.class, D.class);
        assertArchetype(registrations, expected, "baseE", A.class, B.class, E.class);
        assertArchetype(registrations, expected, "baseCD", A.class, B.class, C.class, D.class);
        assertArchetype(registrations, expected, "baseCE", A.class, B.class, C.class, E.class);
        assertArchetype(registrations, expected, "baseDE", A.class, B.class, D.class, E.class);
        assertArchetype(registrations, expected, "baseCDE", A.class, B.class, C.class, D.class, E.class);
    }

    private void assertArchetype(RegistrationMap registrations, Set<Class<?>> expectedSet, String expectedName,
                                 Class<?>... expectedComposition) {
        expectedSet.clear();
        expectedSet.addAll(Arrays.asList(expectedComposition));
        Assertions.assertEquals(expectedSet, registrations.get(Archetype.class, expectedName).getComposition());
    }
}
