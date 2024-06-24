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
import com.sharkecs.builder.EngineBuilder;

/**
 * {@link Configurator} of {@link Initializable}. Calling
 * {@link Initializable#initialize()}, usually after injection.
 *
 * @author Joannick Gardize
 */
public class InitializableConfigurator extends PrioritizedTypeConfigurator<Initializable> {

    public InitializableConfigurator() {
        super(Initializable.class);
    }

    @Override
    protected void prioritizedConfigure(Initializable object, EngineBuilder engineBuilder) {
        object.initialize();
    }

}
