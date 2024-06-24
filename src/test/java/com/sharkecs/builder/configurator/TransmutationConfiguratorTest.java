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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransmutationConfiguratorTest {

    @SuppressWarnings("unchecked")
    @Test
    void configureTest() {

        ComponentMapper<Object> m1 = new FlatArrayComponentMapper<>(0, Object::new);
        ComponentMapper<Object> m2 = new FlatArrayComponentMapper<>(0, Object::new);
        ComponentMapper<Object> m3 = new FlatArrayComponentMapper<>(0, Object::new);
        ComponentMapper<Object> m4 = new FlatArrayComponentMapper<>(0, Object::new);

        Subscription s1 = new Subscription();
        Subscription s2 = new Subscription();
        Subscription s3 = new Subscription();

        Archetype a = new Archetype("a");
        a.setId(0);
        a.setAutoCreateComponentMappers(new ComponentMapper[]{m1, m2});
        a.setComponentMappers(new ComponentMapper[]{m1, m2});
        a.setSubscriptions(new Subscription[]{s1, s2});
        a.setTransmutations(new Transmutation[2]);

        Archetype b = new Archetype("b");
        b.setId(1);
        b.setAutoCreateComponentMappers(new ComponentMapper[]{m2, m3});
        b.setComponentMappers(new ComponentMapper[]{m2, m3, m4});
        b.setSubscriptions(new Subscription[]{s2, s3});

        Transmutation transmutation = new Transmutation(a, b);
        new TransmutationConfigurator().configure(transmutation, null);

        Assertions.assertArrayEquals(transmutation.getAddSubscriptions(), new Subscription[]{s3});
        Assertions.assertArrayEquals(transmutation.getChangeSubscriptions(), new Subscription[]{s2});
        Assertions.assertArrayEquals(transmutation.getRemoveSubscriptions(), new Subscription[]{s1});
        Assertions.assertArrayEquals(transmutation.getAddMappers(), new ComponentMapper[]{m3});
        Assertions.assertArrayEquals(transmutation.getRemoveMappers(), new ComponentMapper[]{m1});
        Assertions.assertEquals(transmutation, a.getTransmutations()[1]);
        Assertions.assertNull(a.getTransmutations()[0]);

        Assertions.assertThrows(IllegalStateException.class, () -> transmutation.setAddMappers(null));
        Assertions.assertThrows(IllegalStateException.class, () -> transmutation.setAddSubscriptions(null));
        Assertions.assertThrows(IllegalStateException.class, () -> transmutation.setChangeSubscriptions(null));
        Assertions.assertThrows(IllegalStateException.class, () -> transmutation.setRemoveMappers(null));
        Assertions.assertThrows(IllegalStateException.class, () -> transmutation.setRemoveSubscriptions(null));
    }
}
