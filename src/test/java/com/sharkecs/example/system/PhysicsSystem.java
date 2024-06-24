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
import com.sharkecs.annotation.SkipInject;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.singleton.Time;
import com.sharkecs.example.system.annotation.LogicPhase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@LogicPhase
@WithAll(Physics.class)
public class PhysicsSystem extends IteratingSystem {

    private ComponentMapper<Physics> physicsMapper;

    private Time time;

    @SkipInject
    private Map<Integer, List<EntityReference>> collisions = new HashMap<>();

    @Override
    public void added(int entityId) {
        Physics physics = physicsMapper.get(entityId);
        physics.setEntityId(entityId);
    }

    @Override
    protected void beginProcess() {
        collisions.values().forEach(List::clear);
    }

    @Override
    protected void process(int entityId) {
        Physics physics = physicsMapper.get(entityId);

        // Update positions
        physics.setPosition(physics.getPosition() + physics.getSpeed() * time.getDeltaTime());

        // Update collisions
        List<EntityReference> collisionGroup = collisions.computeIfAbsent(physics.getPosition(), i -> new ArrayList<>());
        collisionGroup.add(entityManager.reference(entityId));
        physics.setCollisionGroup(collisionGroup);
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
        this.physicsMapper = physicsMapper;
    }
}
