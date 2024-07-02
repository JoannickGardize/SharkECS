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

import sharkhendrix.sharkecs.Engine;
import sharkhendrix.sharkecs.Processor;
import sharkhendrix.sharkecs.builder.EngineBuilder;

/**
 * {@link Configurator} of {@link Processor}. They are added to the
 * {@link Engine}, in their registration order.
 */
public class ProcessorConfigurator extends PrioritizedTypeConfigurator<Processor> {

    public ProcessorConfigurator() {
        super(Processor.class);
    }

    @Override
    protected void prioritizedConfigure(Processor object, EngineBuilder engineBuilder) {
        engineBuilder.addProcessor(object);

    }
}
