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
import com.sharkecs.EntityManager;
import com.sharkecs.SubscriberAdapter;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.example.component.Bullet;
import com.sharkecs.example.component.Corpse;
import com.sharkecs.example.component.Shooter;
import com.sharkecs.example.system.*;
import com.sharkecs.testutil.ArrayTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class ExampleTest {

    @WithAll(Bullet.class)
    private static class BulletSubscriber extends SubscriberAdapter {

    }

    @WithAll(Shooter.class)
    private static class PlayerSubscriber extends SubscriberAdapter {

    }

    @WithAll(Corpse.class)
    private static class CorpseSubscriber extends SubscriberAdapter {

    }

    @Test
    void test() {

        BulletSubscriber bulletSubscriber = new BulletSubscriber();
        PlayerSubscriber playerSubscriber = new PlayerSubscriber();
        CorpseSubscriber corpseSubscriber = new CorpseSubscriber();

        EngineBuilder builder = ExampleBuilder.builder();
        builder.with(bulletSubscriber);
        builder.with(playerSubscriber);
        builder.with(corpseSubscriber);

        Engine engine = builder.build();

        Assertions.assertArrayEquals(new Object[]{EntityManager.class, TimeManager.class, PhysicsSystem.class, BulletDamageSystem.class, BulletLifetimeSystem.class,
                ShootSystem.class, DeathSystem.class}, Arrays.stream(engine.getProcessors()).map(Object::getClass).toArray());
        advance(engine, 2);
        Object[] playerIds = Arrays.stream(playerSubscriber.getEntities().getData()).mapToObj(Integer::valueOf).toArray();
        Assertions.assertEquals(3, playerSubscriber.getEntities().size());
        Assertions.assertEquals(3, bulletSubscriber.getEntities().size());
        Assertions.assertEquals(0, corpseSubscriber.getEntities().size());
        advance(engine, 9);
        Assertions.assertEquals(2, playerSubscriber.getEntities().size());
        Assertions.assertEquals(1, corpseSubscriber.getEntities().size());
        advance(engine, 30);
        Assertions.assertEquals(0, playerSubscriber.getEntities().size());
        Assertions.assertEquals(3, corpseSubscriber.getEntities().size());
        ArrayTestUtils.assertEqualsAnyOrder(playerIds, Arrays.stream(corpseSubscriber.getEntities().getData()).mapToObj(Integer::valueOf).toArray());
    }

    private void advance(Engine engine, int amount) {
        for (int i = 0; i < amount; i++) {
            engine.process();
        }
    }
}
