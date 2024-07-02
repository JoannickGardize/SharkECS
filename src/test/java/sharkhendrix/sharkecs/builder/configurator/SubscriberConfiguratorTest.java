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

package sharkhendrix.sharkecs.builder.configurator;


import org.junit.jupiter.api.Test;
import sharkhendrix.sharkecs.Aspect;
import sharkhendrix.sharkecs.annotation.RequiresEntityTracking;
import sharkhendrix.sharkecs.annotation.SortEntities;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.builder.EngineBuilder;
import sharkhendrix.sharkecs.subscription.SortedTrackingSubscription;
import sharkhendrix.sharkecs.subscription.SubscriberAdapter;
import sharkhendrix.sharkecs.subscription.Subscription;
import sharkhendrix.sharkecs.subscription.TrackingSubscription;
import sharkhendrix.sharkecs.util.IntComparator;
import sharkhendrix.sharkecs.util.SortableIntArrayList;
import sharkhendrix.sharkecs.util.SortableIntList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberConfiguratorTest {

    private List<String> log = new ArrayList<>();

    @With(Integer.class)
    private class A extends SubscriberAdapter {
        @Override
        public void added(int entity) {
            log.add("A");
        }

    }

    @With(Integer.class)
    @RequiresEntityTracking(false)
    private class B extends SubscriberAdapter {
        @Override
        public void added(int entity) {
            log.add("B");
        }
    }

    @With(Long.class)
    @RequiresEntityTracking(false)
    private class C extends SubscriberAdapter {

    }

    @With(Double.class)
    @RequiresEntityTracking(false)
    private class D extends SubscriberAdapter {

    }

    @With(Double.class)
    private class E extends SubscriberAdapter {

    }

    @With(Double.class)
    @SortEntities("testSort")
    private class F extends SubscriberAdapter {

    }

    @With(Double.class)
    @SortEntities("testSort2")
    private class G extends SubscriberAdapter {

    }

    private class Sort implements IntComparator {

        @Override
        public int compare(int entity1, int entity2) {
            return 0;
        }
    }

    @Test
    void configureTest() {

        Sort sort1 = new Sort();
        Sort sort2 = new Sort();
        EngineBuilder builder = new EngineBuilder();
        builder.with(new A()).with(new B()).with(new C()).with(new D())
                .with(new E()).with(new F()).with(new G())
                .entitySort("testSort", sort1)
                .entitySort("testSort2", sort2);

        Prioritizer prioritizer = new Prioritizer();
        builder.with(prioritizer);
        builder.before(B.class, A.class);
        prioritizer.configure(builder);

        new SubscriberConfigurator().configure(builder);

        assertEquals(3, builder.getRegistrations().typeCount(SubscriptionGroup.class));
        SubscriptionGroup group1 = builder.getRegistrations().get(SubscriptionGroup.class, new Aspect(A.class));
        assertEquals(1, group1.getSubscriptionsBySort().size());
        SubscriptionGroup group2 = builder.getRegistrations().get(SubscriptionGroup.class, new Aspect(C.class));
        assertEquals(1, group2.getSubscriptionsBySort().size());
        SubscriptionGroup group3 = builder.getRegistrations().get(SubscriptionGroup.class, new Aspect(D.class));
        assertEquals(1, group2.getSubscriptionsBySort().size());
        Subscription s1 = group1.getSubscriptionsBySort().get(null);
        Subscription s2 = group2.getSubscriptionsBySort().get(null);
        Subscription s3 = group3.getSubscriptionsBySort().get("testSort");
        Subscription s4 = group3.getSubscriptionsBySort().get("testSort2");
        Subscription s5 = group3.getSubscriptionsBySort().get(null);
        assertNotNull(s1);
        assertSame(TrackingSubscription.class, s1.getClass());
        assertNotNull(s2);
        assertSame(Subscription.class, s2.getClass());
        assertNotNull(s3);
        assertSame(SortedTrackingSubscription.class, s3.getClass());
        assertNotNull(s4);
        assertSame(SortedTrackingSubscription.class, s4.getClass());
        assertTrue(s5 == s3 || s5 == s4);
        SortableIntList l3 = ((SortedTrackingSubscription) s3).entities();
        assertSame(SortableIntArrayList.class, l3.getClass());
        assertSame(sort1, ((SortableIntArrayList) l3).getComparator());
        SortableIntList l4 = ((SortedTrackingSubscription) s4).entities();
        assertSame(SortableIntArrayList.class, l4.getClass());
        assertSame(sort2, ((SortableIntArrayList) l4).getComparator());

        log.clear();
        s1.add(0);
        assertEquals(Arrays.asList("B", "A"), log);
    }
}
