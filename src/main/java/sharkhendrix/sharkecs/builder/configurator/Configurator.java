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
 * A configurator of something, called during {@link EngineBuilder#build()}
 * (when using the default {@link RootConfigurator}).
 */
public interface Configurator {

    /**
     * @param engineBuilder the EngineBuilder to configure
     */
    void configure(EngineBuilder engineBuilder);
}
