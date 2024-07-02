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

/**
 * A {@link Configurator} iterating other all registered objects assignable from
 * a given type.
 *
 * @param <T>
 */
public abstract class TypeConfigurator<T> implements Configurator {

    private Class<T> type;

    protected TypeConfigurator(Class<T> type) {
        this.type = type;
    }

    @Override
    public void configure(EngineBuilder engineBuilder) {
        beginConfiguration(engineBuilder);
        engineBuilder.getRegistrations().forEachAssignableFrom(type, o -> configure(o, engineBuilder));
        endConfiguration(engineBuilder);
    }

    protected void beginConfiguration(EngineBuilder engineBuilder) {
        // Nothing by default
    }

    protected abstract void configure(T object, EngineBuilder engineBuilder);

    protected void endConfiguration(EngineBuilder engineBuilder) {
        // Nothing by default
    }

}
