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

package sharkhendrix.sharkecs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.builder.EngineBuilder;
import sharkhendrix.sharkecs.builder.RegistrationMap;
import sharkhendrix.sharkecs.subscription.SubscriberAdapter;
import sharkhendrix.sharkecs.subscription.Subscription;
import sharkhendrix.sharkecs.subscription.SubscriptionListener;
import sharkhendrix.sharkecs.testutil.SubscriptionLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class EntityManagerTest {

    static class A {
    }

    static class B {
    }

    static class C {
    }

    private Archetype archetype1;
    private Archetype archetype2;
    private Archetype archetype3;

    private ComponentMapper<A> mapperA;
    private ComponentMapper<B> mapperB;
    private ComponentMapper<C> mapperC;

    private Subscription subscriptionA;
    private SubscriptionLogger listenerA;
    private Subscription subscriptionB;
    private SubscriptionLogger listenerB;
    private Subscription subscriptionC;
    private SubscriptionLogger listenerC;

    private Transmutation transmutation;
    private Transmutation transmutation2;
    private Transmutation transmutation3;

    private EntityManager manager;

    @With(A.class)
    private static class SubscriberA extends SubscriberAdapter {

    }

    @With(B.class)
    private static class SubscriberB extends SubscriberAdapter {

    }

    @With(C.class)
    private static class SubscriberC extends SubscriberAdapter {

    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void initialize() {

        SubscriberA subscriberA = new SubscriberA();
        SubscriberB subscriberB = new SubscriberB();
        SubscriberC subscriberC = new SubscriberC();

        EngineBuilder builder = EngineBuilder.withDefaults(10)
                .defaultComponentCreationPolicy(Archetype.ComponentCreationPolicy.AUTOMATIC)

                .component(A.class, A::new)
                .component(B.class, B::new)
                .component(C.class, C::new)

                .archetype("archetype1", A.class, B.class)
                .archetype("archetype2", C.class)
                .archetype("archetype3", B.class, C.class)

                .with(subscriberA)
                .with(subscriberB)
                .with(subscriberC)

                .transmutation("archetype1", "archetype3")
                .transmutation("archetype2", "archetype3")
                .transmutation("archetype3", "archetype2");

        archetype1 = builder.getRegistrations().get(Archetype.class, "archetype1");
        archetype2 = builder.getRegistrations().get(Archetype.class, "archetype2");
        archetype3 = builder.getRegistrations().get(Archetype.class, "archetype3");

        for (Map.Entry<Object, Transmutation> set : builder.getRegistrations().entrySet(Transmutation.class)) {
            Transmutation transmutation = set.getValue();
            if (transmutation.getFrom() == archetype1) {
                this.transmutation = transmutation;
            } else if (transmutation.getFrom() == archetype2) {
                transmutation2 = transmutation;
            } else if (transmutation.getFrom() == archetype3) {
                transmutation3 = transmutation;
            }
        }

        builder.build();

        RegistrationMap registrationMap = builder.getRegistrations();

        mapperA = registrationMap.get(ComponentMapper.class, A.class);
        mapperB = registrationMap.get(ComponentMapper.class, B.class);
        mapperC = registrationMap.get(ComponentMapper.class, C.class);

        subscriptionA = subscriberA.getSubscription();
        listenerA = new SubscriptionLogger();
        subscriptionA.addListener(listenerA);
        subscriptionB = subscriberB.getSubscription();
        listenerB = new SubscriptionLogger();
        subscriptionB.addListener(listenerB);
        subscriptionC = subscriberC.getSubscription();
        listenerC = new SubscriptionLogger();
        subscriptionC.addListener(listenerC);

        manager = registrationMap.get(EntityManager.class);
    }

    @Test
    void createAndRemoveTest() {

        // Creation test

        List<Integer> managerIdLog = new ArrayList<>();
        managerIdLog.add(manager.create(archetype1));
        managerIdLog.add(manager.create(archetype1));
        managerIdLog.add(manager.create(archetype2));

        Assertions.assertEquals(Arrays.asList(0, 1, 2), managerIdLog);
        Assertions.assertNotNull(mapperA.get(0));
        Assertions.assertNotNull(mapperB.get(0));
        Assertions.assertNull(mapperC.get(0));

        manager.process();

        Assertions.assertEquals(archetype1, manager.archetypeOf(0));
        Assertions.assertEquals(archetype1, manager.archetypeOf(1));
        Assertions.assertEquals(archetype2, manager.archetypeOf(2));
        listenerA.assertAddLog(0, 1);
        listenerA.assertRemoveLog();
        listenerC.assertAddLog(2);
        listenerC.assertRemoveLog();

        clearListeners();

        // Remove & insertion test

        subscriptionA.addListener(new SubscriptionListener() {

            @Override
            public void removed(int entity) {
                Assertions.assertNotNull(mapperA.get(entity));
                Assertions.assertNotNull(mapperB.get(entity));
            }

            @Override
            public void added(int entity) {
            }

            @Override
            public void changed(int entity, Transmutation transmutation) {
            }
        });

        manager.remove(1);
        manager.remove(0);
        manager.create(archetype2);
        manager.create(archetype2);

        manager.process();

        listenerA.assertAddLog();
        listenerA.assertRemoveLog(1, 0);
        listenerC.assertAddLog(3, 4);
        listenerC.assertRemoveLog();

        Assertions.assertNull(mapperA.get(0));
        Assertions.assertNull(mapperB.get(0));
        Assertions.assertNotNull(mapperC.get(3));

        // Insertion with recycling test

        Assertions.assertEquals(0, manager.create(archetype1));
        Assertions.assertEquals(1, manager.create(archetype1));
        Assertions.assertEquals(5, manager.create(archetype1));

        manager.process();

        listenerA.assertAddLog(0, 1, 5);

        checkEmptyRun();
    }

    @Test
    void transmuteTest() {
        int id = manager.create(archetype1);

        manager.process();

        clearListeners();

        subscriptionA.addListener(new SubscriptionListener() {

            @Override
            public void removed(int entity) {
                Assertions.assertNotNull(mapperA.get(entity));
            }

            @Override
            public void added(int entity) {
            }

            @Override
            public void changed(int entity, Transmutation transmutation) {
            }
        });

        manager.transmute(id, archetype3);

        manager.process();

        listenerA.assertAddLog();
        listenerA.assertChangeLog();
        listenerA.assertRemoveLog(0);

        listenerB.assertAddLog();
        listenerB.assertChangeLog(0);
        listenerB.assertTransmutationLog(transmutation);

        listenerB.assertRemoveLog();

        listenerC.assertAddLog(0);
        listenerC.assertChangeLog();
        listenerC.assertRemoveLog();

        Assertions.assertNull(mapperA.get(0));
        Assertions.assertNotNull(mapperB.get(0));
        Assertions.assertNotNull(mapperC.get(0));
        Assertions.assertEquals(archetype3, manager.archetypeOf(0));

        checkEmptyRun();

    }

    @Test
    void addComponentTest() {
        int id = manager.create(archetype2);

        manager.process();

        clearListeners();

        manager.addComponent(id, B.class);

        manager.process();

        listenerB.assertAddLog(id);

        Assertions.assertNull(mapperA.get(id));
        Assertions.assertNotNull(mapperB.get(id));
        Assertions.assertNotNull(mapperC.get(id));
        Assertions.assertEquals(archetype3, manager.archetypeOf(id));

        checkEmptyRun();

    }

    @Test
    void removeComponentTest() {
        int id = manager.create(archetype3);

        manager.process();

        clearListeners();

        manager.removeComponent(id, B.class);

        manager.process();

        listenerB.assertRemoveLog(id);

        Assertions.assertNull(mapperA.get(id));
        Assertions.assertNull(mapperB.get(id));
        Assertions.assertNotNull(mapperC.get(id));
        Assertions.assertEquals(archetype2, manager.archetypeOf(id));

        checkEmptyRun();

    }

    @Test
    void addThenRemoveComponentTest() {
        int id = manager.create(archetype2);

        manager.process();

        clearListeners();

        manager.addComponent(id, B.class);
        manager.removeComponent(id, B.class);

        manager.process();

        listenerB.assertAddLog(id);
        listenerB.assertRemoveLog(id);
        listenerB.assertTransmutationLog();
        listenerA.assertAddLog();
        listenerA.assertRemoveLog();
        listenerA.assertTransmutationLog();
        listenerC.assertAddLog();
        listenerC.assertRemoveLog();
        listenerC.assertTransmutationLog(transmutation2, transmutation3);

        Assertions.assertNull(mapperA.get(id));
        Assertions.assertNull(mapperB.get(id));
        Assertions.assertNotNull(mapperC.get(id));
        Assertions.assertEquals(archetype2, manager.archetypeOf(id));

        checkEmptyRun();

    }

    private void checkEmptyRun() {
        clearListeners();

        manager.process();

        listenerA.assertAddLog();
        listenerA.assertChangeLog();
        listenerA.assertTransmutationLog();
        listenerA.assertRemoveLog();

        listenerB.assertAddLog();
        listenerB.assertChangeLog();
        listenerB.assertTransmutationLog();
        listenerB.assertRemoveLog();

        listenerC.assertAddLog();
        listenerC.assertChangeLog();
        listenerC.assertTransmutationLog();
        listenerC.assertRemoveLog();
    }

    private void clearListeners() {
        listenerA.clear();
        listenerB.clear();
        listenerC.clear();
    }
}
