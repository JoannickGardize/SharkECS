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

package com.sharkecs.example;

import com.sharkecs.Engine;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.example.system.ConsoleCleaner;
import com.sharkecs.example.system.EntityDrawingSystem;
import com.sharkecs.example.system.HeaderDrawer;
import com.sharkecs.example.system.HealthDrawerSystem;
import com.sharkecs.example.system.annotation.DrawingPhase;
import com.sharkecs.example.system.annotation.LogicPhase;

public class ExampleConsole {

    public static void main(String[] args) throws InterruptedException {
        EngineBuilder builder = ExampleBuilder.builder();

        builder.with(new ConsoleCleaner());
        builder.then(new HeaderDrawer());
        builder.then(new HealthDrawerSystem());
        builder.then(new EntityDrawingSystem());

        builder.before(LogicPhase.class, DrawingPhase.class);

        Engine engine = builder.build();

        for (int i = 0; i < 40; i++) {
            engine.process();
            Thread.sleep(500);
        }
    }
}
