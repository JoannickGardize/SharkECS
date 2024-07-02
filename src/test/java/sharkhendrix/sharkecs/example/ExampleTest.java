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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sharkhendrix.sharkecs.Engine;
import sharkhendrix.sharkecs.EntityManager;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.annotation.Without;
import sharkhendrix.sharkecs.builder.EngineBuilder;
import sharkhendrix.sharkecs.example.component.Bullet;
import sharkhendrix.sharkecs.example.component.Shooter;
import sharkhendrix.sharkecs.example.system.*;
import sharkhendrix.sharkecs.subscription.SubscriberAdapter;
import sharkhendrix.sharkecs.testutil.ArrayTestUtils;

import java.util.Arrays;

class ExampleTest {

    @With(Bullet.class)
    private static class BulletSubscriber extends SubscriberAdapter {

    }

    @With(Shooter.class)
    private static class PlayerSubscriber extends SubscriberAdapter {

    }

    @Without({Shooter.class, Bullet.class})
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
        Object[] playerIds = Arrays.stream(playerSubscriber.entityIterator().toArray()).boxed().toArray();
        Assertions.assertEquals(3, playerSubscriber.entityIterator().totalSize());
        Assertions.assertEquals(3, bulletSubscriber.entityIterator().totalSize());
        Assertions.assertEquals(0, corpseSubscriber.entityIterator().totalSize());
        advance(engine, 9);
        Assertions.assertEquals(2, playerSubscriber.entityIterator().totalSize());
        Assertions.assertEquals(1, corpseSubscriber.entityIterator().totalSize());
        advance(engine, 30);
        Assertions.assertEquals(0, playerSubscriber.entityIterator().totalSize());
        Assertions.assertEquals(3, corpseSubscriber.entityIterator().totalSize());
        ArrayTestUtils.assertEqualsAnyOrder(playerIds, Arrays.stream(corpseSubscriber.entityIterator().toArray()).boxed().toArray());
    }

    private void advance(Engine engine, int amount) {
        for (int i = 0; i < amount; i++) {
            engine.process();
        }
    }
}
