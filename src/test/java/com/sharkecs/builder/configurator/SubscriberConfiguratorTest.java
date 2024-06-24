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

import com.sharkecs.Aspect;
import com.sharkecs.SubscriberAdapter;
import com.sharkecs.Subscription;
import com.sharkecs.TrackingSubscription;
import com.sharkecs.annotation.RequiresEntityTracking;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.builder.EngineBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
