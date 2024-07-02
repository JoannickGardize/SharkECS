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

package sharkhendrix.sharkecs.example.system;

import sharkhendrix.sharkecs.ComponentMapper;
import sharkhendrix.sharkecs.EntityReference;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.example.component.Bullet;
import sharkhendrix.sharkecs.example.component.Health;
import sharkhendrix.sharkecs.example.component.Physics;
import sharkhendrix.sharkecs.example.system.annotation.LogicPhase;
import sharkhendrix.sharkecs.system.IteratingSystem;

import javax.swing.text.Position;

@LogicPhase
@With({Physics.class, Bullet.class})
public class BulletDamageSystem extends IteratingSystem {

    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Bullet> bulletMapper;
    private ComponentMapper<Health> healthMapper;

    private Position p;

    @Override
    public void process(int entity) {
        Physics physics = physicsMapper.get(entity);
        for (EntityReference entityRef : physics.getCollisionGroup()) {
            entityRef.ifExists(collidingId ->
                    healthMapper.ifExists(collidingId, health -> {
                        Bullet bullet = bulletMapper.get(entity);
                        health.takeDamage(bullet.getDamage());
                        entityManager.remove(entity);
                    }));
        }
    }

    public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
        this.physicsMapper = physicsMapper;
    }

    public void setBulletMapper(ComponentMapper<Bullet> bulletMapper) {
        this.bulletMapper = bulletMapper;
    }

    public void setHealthMapper(ComponentMapper<Health> healthMapper) {
        this.healthMapper = healthMapper;
    }
}
