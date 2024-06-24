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

import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.example.component.*;
import com.sharkecs.example.singleton.Time;
import com.sharkecs.example.singleton.Viewport;
import com.sharkecs.example.system.*;

public class ExampleBuilder {

    public static EngineBuilder builder() {

        EngineBuilder builder = EngineBuilder.withDefaults()

                // Register component types
                .component(Physics.class, Physics::new)
                .component(Bullet.class, Bullet::new)
                .component(Health.class, Health::new)
                .component(Shooter.class, Shooter::new)
                .component(Corpse.class, Corpse::new)

                // Register entity archetypes
                .archetype("player", Physics.class, Health.class, Shooter.class)
                .archetype("corpse", Physics.class, Corpse.class)
                .archetype("bullet", Physics.class, Bullet.class)

                // Register transmutations
                .transmutation("player", "corpse")

                // Register managers & systems, in the right order
                .with(new TimeManager())
                .then(new PhysicsSystem())
                .then(new BulletDamageSystem())
                .then(new BulletLifetimeSystem())
                .then(new ShootSystem())
                .then(new DeathSystem())

                // Register miscellaneous stuff
                .with(new Time())
                .with(new Viewport())
                .with(new ExampleScenarioInitializer());

        return builder;
    }
}
