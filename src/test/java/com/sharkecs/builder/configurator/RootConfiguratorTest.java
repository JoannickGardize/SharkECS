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

import com.sharkecs.annotation.BeforeAll;
import com.sharkecs.builder.EngineBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class RootConfiguratorTest {

    private static List<Object> logList = new ArrayList<>();

    private static class LogConfigurator implements Configurator {

        @Override
        public void configure(EngineBuilder engineBuilder) {
            logList.add(getClass());
        }

    }

    private static class B extends LogConfigurator {

    }

    @BeforeAll
    private static class BeforeConfigurator extends LogConfigurator {

    }

    private static class A extends LogConfigurator {

    }

    @BeforeEach
    void initialize() {
        logList.clear();
    }

    @Test
    void configureTest() {
        EngineBuilder builder = new EngineBuilder(1, new RootConfigurator());
        builder.with(new Prioritizer());
        builder.with(new A());
        builder.then(new B());
        builder.with(new BeforeConfigurator());

        builder.build();

        Assertions.assertEquals(Arrays.asList(BeforeConfigurator.class, A.class, B.class), logList);
    }
}
