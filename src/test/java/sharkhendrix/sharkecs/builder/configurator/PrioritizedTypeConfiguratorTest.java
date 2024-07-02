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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PrioritizedTypeConfiguratorTest {

    private static class A implements Processor {

        @Override
        public void process() {
        }
    }

    private static class B implements Processor {

        @Override
        public void process() {
        }

    }

    @Test
    void configureTest() {
        EngineBuilder builder = new EngineBuilder();
        builder.with(new Prioritizer());
        A a = new A();
        B b = new B();
        builder.with(new Object());
        builder.with(a);
        builder.then(b);
        builder.with(2);

        List<Object> logList = new ArrayList<>();

        builder.getRegistrations().get(Prioritizer.class).configure(builder);
        new PrioritizedTypeConfigurator<Processor>(Processor.class) {

            @Override
            protected void prioritizedConfigure(Processor object, EngineBuilder engineBuilder) {
                logList.add(object);
            }

        }.configure(builder);

        Assertions.assertArrayEquals(new Object[]{a, b}, logList.toArray());
    }
}
