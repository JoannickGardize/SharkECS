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

package com.sharkecs.builder.configurator;

import com.sharkecs.*;
import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.builder.EngineConfigurationException;
import com.sharkecs.testutil.ArrayTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArchetypeConfiguratorTest {

    @WithAll(Short.class)
    static class A {

    }

    @WithAll({Short.class, Integer.class})
    static class B {

    }

    @WithAll(Double.class)
    static class C {

    }

    @Test
    void configureTest() {

        ComponentMapper<Short> shortMapper = new FlatArrayComponentMapper<>(0, () -> (short) 0);
        ComponentMapper<Integer> intMapper = new FlatArrayComponentMapper<>(0, () -> 0);
        ComponentMapper<Long> longMapper = new FlatArrayComponentMapper<>(0, () -> 0L);
        EngineBuilder builder = new EngineBuilder()
                .component(Short.class, shortMapper)
                .component(Integer.class, intMapper)
                .component(Long.class, longMapper)

                .archetype("test", Short.class, Integer.class, Long.class)
                .componentCreationPolicy(ComponentCreationPolicy.MANUAL, Short.class)
                .archetype("test2", Short.class);

        Subscription sA = new Subscription();
        builder.getRegistrations().put(new Aspect(A.class), sA);
        Subscription sB = new Subscription();
        builder.getRegistrations().put(new Aspect(B.class), sB);
        Subscription sC = new Subscription();
        builder.getRegistrations().put(new Aspect(C.class), sC);

        Archetype a = builder.getRegistrations().get(Archetype.class, "test");

        ArchetypeConfigurator configurator = new ArchetypeConfigurator();
        configurator.setDefaultComponentCreationPolicy(ComponentCreationPolicy.AUTOMATIC);

        Assertions.assertThrows(EngineConfigurationException.class, () -> configurator.of(Short.class));

        configurator.configure(a, builder);
        configurator.endConfiguration(builder);

        ArrayTestUtils.assertEqualsAnyOrder(a.getComponentMappers(), shortMapper, intMapper, longMapper);
        ArrayTestUtils.assertEqualsAnyOrder(a.getAutoCreateComponentMappers(), intMapper, longMapper);
        ArrayTestUtils.assertEqualsAnyOrder(a.getSubscriptions(), sA, sB);
        Assertions.assertEquals(2, a.getTransmutations().length);

        Assertions.assertEquals(a, configurator.of(Short.class, Integer.class, Long.class));
        Assertions.assertNull(configurator.of(Long.class));

        Assertions.assertThrows(IllegalStateException.class, () -> a.setId(0));
        Assertions.assertThrows(IllegalStateException.class, () -> a.setComponentCreationPolicy(null, Integer.class));
        Assertions.assertThrows(IllegalStateException.class, () -> a.setComponentCreationPolicy(null));
        Assertions.assertThrows(IllegalStateException.class, () -> a.setAutoCreateComponentMappers(null));
        Assertions.assertThrows(IllegalStateException.class, () -> a.setComponentMappers(null));
        Assertions.assertThrows(IllegalStateException.class, () -> a.setSubscriptions(null));
        Assertions.assertThrows(IllegalStateException.class, () -> a.setTransmutations(null));
    }

}
