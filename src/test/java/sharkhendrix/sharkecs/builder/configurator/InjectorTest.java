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

import sharkhendrix.sharkecs.annotation.ForceInject;
import sharkhendrix.sharkecs.annotation.Inject;
import sharkhendrix.sharkecs.annotation.SkipInject;
import sharkhendrix.sharkecs.builder.EngineBuilder;
import sharkhendrix.sharkecs.builder.EngineConfigurationException;
import sharkhendrix.sharkecs.builder.RegistrationMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        registrations.put(new C());

        builder.autoInjectType(AutoInject.class);
    }

    private static interface AutoInject {
    }

    @SuppressWarnings("unused")
    private static class A implements AutoInject {
        private List<Integer> intList;
        private Long l1;
        private Long fieldName;
        @SkipInject
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
        private D d;

        public void setTheLongList(List<Long> theLongList) {
            this.theLongList = theLongList;
        }

        public void setD(D d) {
            this.d = d;
        }
    }

    @Inject(injectParent = true)
    private static class C extends D {
    }

    @Inject
    @SuppressWarnings("unused")
    private static class D {
        private Integer i;

        @ForceInject
        private Long forced;

        public void setI(Integer i) {
            this.i = i;
        }

        public void setForced(Long forced) {
            this.forced = forced;
        }

    }

    @SkipInject
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
        @SkipInject
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
        RegistrationMap registrations = builder.getRegistrations();
        B b = new B();
        injector.inject(b, registrations);
        Assertions.assertSame(longList, b.theLongList);
        Assertions.assertNull(((D) b).i);
        Assertions.assertEquals(1L, ((D) b).forced);
        Assertions.assertNull(b.d);

        b = new B();
        injector.setInjectAnyAssignableType(true);
        injector.inject(b, registrations);
        Assertions.assertEquals(C.class, b.d.getClass());

        injector.setFailWhenNotFound(true);
        injector.setInjectAnyAssignableType(false);
        B b2 = new B();
        Assertions.assertThrows(EngineConfigurationException.class, () -> injector.inject(b2, registrations));
    }

    @Test
    void testC() {
        C c = new C();
        injector.inject(c, builder.getRegistrations());
        Assertions.assertEquals(3, ((D) c).i);
        Assertions.assertEquals(1L, ((D) c).forced);
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
