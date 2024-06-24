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

import com.sharkecs.Engine;
import com.sharkecs.Processor;
import com.sharkecs.builder.EngineBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class ProcessorConfiguratorTest {

    @Test
    void configureTest() {
        EngineBuilder engineBuilder = new EngineBuilder();
        engineBuilder.with(new Prioritizer());
        engineBuilder.then(new ProcessorConfigurator());
        Processor p = () -> {
        };
        engineBuilder.with(p);
        Engine engine = engineBuilder.build();
        Assertions.assertEquals(Arrays.asList(p), Arrays.asList(engine.getProcessors()));
    }
}
