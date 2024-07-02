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

import sharkhendrix.sharkecs.Processor;
import sharkhendrix.sharkecs.builder.EngineBuilder;
import sharkhendrix.sharkecs.testutil.ArrayTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class TypeConfiguratorTest {

    static class A implements Processor {

        @Override
        public void process() {
        }

    }

    static class B implements Processor {

        @Override
        public void process() {
        }

    }

    @Test
    void configureTest() {

        List<Object> logList = new ArrayList<Object>();

        TypeConfigurator<Processor> typeConfigurator = new TypeConfigurator<Processor>(Processor.class) {

            @Override
            protected void beginConfiguration(EngineBuilder engineBuilder) {
                logList.add("beginConfiguration");
            }

            @Override
            protected void configure(Processor object, EngineBuilder engineBuilder) {
                logList.add(object);
            }

            @Override
            protected void endConfiguration(EngineBuilder engineBuilder) {
                logList.add("endConfiguration");
            }
        };

        A a = new A();
        B b = new B();

        EngineBuilder builder = new EngineBuilder();
        builder.with(a);
        builder.with(b);
        builder.with(new Object());
        builder.with("test");

        typeConfigurator.configure(builder);

        Assertions.assertEquals(4, logList.size());
        Assertions.assertEquals("beginConfiguration", logList.get(0));
        Assertions.assertEquals("endConfiguration", logList.get(3));
        ArrayTestUtils.assertEqualsAnyOrder(new Object[]{a, b}, logList.get(1), logList.get(2));
    }
}
