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
import sharkhendrix.sharkecs.annotation.SkipInject;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.example.component.Physics;
import sharkhendrix.sharkecs.example.misc.Time;
import sharkhendrix.sharkecs.example.system.annotation.LogicPhase;
import sharkhendrix.sharkecs.system.IteratingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@LogicPhase
@With(Physics.class)
public class PhysicsSystem extends IteratingSystem {

    private ComponentMapper<Physics> physicsMapper;

    private Time time;

    @SkipInject
    private Map<Integer, List<EntityReference>> collisions = new HashMap<>();

    @Override
    protected void beginProcess() {
        collisions.values().forEach(List::clear);
    }

    @Override
    protected void process(int entity) {
        Physics physics = physicsMapper.get(entity);

        // Update positions
        physics.setPosition(physics.getPosition() + physics.getSpeed() * time.getDeltaTime());

        // Update collisions
        List<EntityReference> collisionGroup = collisions.computeIfAbsent(physics.getPosition(), i -> new ArrayList<>());
        collisionGroup.add(entityManager.reference(entity));
        physics.setCollisionGroup(collisionGroup);
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
        this.physicsMapper = physicsMapper;
    }
}
