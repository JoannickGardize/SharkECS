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

package com.sharkecs.example.system;

import com.sharkecs.ComponentMapper;
import com.sharkecs.EntityReference;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Bullet;
import com.sharkecs.example.component.Health;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@WithAll({Physics.class, Bullet.class})
public class BulletDamageSystem extends IteratingSystem {

    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Bullet> bulletMapper;
    private ComponentMapper<Health> healthMapper;

    @Override
    public void process(int entityId) {
        Physics physics = physicsMapper.get(entityId);
        for (EntityReference entityRef : physics.getCollisionGroup()) {
            entityRef.ifExists(collidingId ->
                    healthMapper.ifExists(collidingId, health -> {
                        Bullet bullet = bulletMapper.get(entityId);
                        health.takeDamage(bullet.getDamage());
                        entityManager.remove(entityId);
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
