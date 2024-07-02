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

import sharkhendrix.sharkecs.builder.EngineBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link TypeConfigurator} using the {@link Prioritizer} to iterate over
 * elements according to their priority.
 *
 * @param <T>
 */
public abstract class PrioritizedTypeConfigurator<T> extends TypeConfigurator<T> {

    private List<T> elements = new ArrayList<>();

    protected PrioritizedTypeConfigurator(Class<T> type) {
        super(type);
    }

    @Override
    protected final void configure(T object, EngineBuilder engineBuilder) {
        elements.add(object);
    }

    @Override
    protected void endConfiguration(EngineBuilder engineBuilder) {
        engineBuilder.getRegistrations().get(Prioritizer.class).prioritize(elements);
        elements.forEach(o -> prioritizedConfigure(o, engineBuilder));
    }

    protected abstract void prioritizedConfigure(T object, EngineBuilder engineBuilder);
}
