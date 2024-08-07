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

package sharkhendrix.sharkecs;

import sharkhendrix.sharkecs.builder.EngineBuilder;

/**
 * Root class of an ECS run. It is simply made of an array of {@link Processor}.
 * It is intended to be created via an {@link EngineBuilder
 * EngineBuilder}, which will configure and link all elements together.
 */
public class Engine {

    private Processor[] processors;

    public Engine(Processor[] processors) {
        this.processors = processors;
    }

    /**
     * Process sequentially all {@link Processor}s of this engine. This is intended
     * to be called successively, for instance, every frame of a game or a
     * simulation.
     */
    public void process() {
        for (Processor processor : processors) {
            processor.process();
        }
    }

    public Processor[] getProcessors() {
        return processors;
    }
}
