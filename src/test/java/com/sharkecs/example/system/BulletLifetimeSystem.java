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
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Bullet;
import com.sharkecs.example.singleton.Time;
import com.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@WithAll(Bullet.class)
public class BulletLifetimeSystem extends IteratingSystem {

    private ComponentMapper<Bullet> bulletMapper;

    private Time time;

    @Override
    protected void process(int entityId) {
        Bullet bullet = bulletMapper.get(entityId);
        if (time.getElapsedTime() >= bullet.getDeathTime()) {
            entityManager.remove(entityId);
        }
    }

    public void setBulletMapper(ComponentMapper<Bullet> bulletMapper) {
        this.bulletMapper = bulletMapper;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
