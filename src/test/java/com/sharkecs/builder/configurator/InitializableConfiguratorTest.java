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

import com.sharkecs.Initializable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InitializableConfiguratorTest {

    private boolean called;

    @Test
    void configureTest() {
        Initializable initializable = () -> called = true;

        called = false;
        new InitializableConfigurator().prioritizedConfigure(initializable, null);
        Assertions.assertTrue(called);
    }
}
