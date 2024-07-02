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

package sharkhendrix.sharkecs.example;

import sharkhendrix.sharkecs.builder.EngineBuilder;
import sharkhendrix.sharkecs.example.component.*;
import sharkhendrix.sharkecs.example.misc.Time;
import sharkhendrix.sharkecs.example.misc.Viewport;
import sharkhendrix.sharkecs.example.system.*;

public class ExampleBuilder {

    public static EngineBuilder builder() {

        EngineBuilder builder = EngineBuilder.withDefaults()

                // Register component types
                .component(Physics.class, Physics::new)
                .component(Bullet.class, Bullet::new)
                .component(Health.class, Health::new)
                .component(Shooter.class, Shooter::new)
                .component(Image.class, Image::new)

                // Register entity archetypes
                .archetype("player", Shooter.class, Health.class, Image.class, Physics.class)
                .archetype("corpse", Image.class, Physics.class)
                .archetype("bullet", Bullet.class, Image.class, Physics.class)

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
