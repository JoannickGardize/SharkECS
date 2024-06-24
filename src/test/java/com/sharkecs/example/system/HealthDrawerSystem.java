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
import com.sharkecs.example.component.Health;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.singleton.Viewport;
import com.sharkecs.example.system.annotation.DrawingPhase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@DrawingPhase
@WithAll({Physics.class, Health.class})
public class HealthDrawerSystem extends IteratingSystem {

    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Health> healthMapper;

    private Viewport viewport;

    private List<Physics> batch = new ArrayList<>();

    @Override
    protected void beginProcess() {
        batch.clear();
    }

    @Override
    protected void process(int entityId) {
        batch.add(physicsMapper.get(entityId));
    }

    @Override
    protected void endProcess() {
        batch.sort(Comparator.comparingInt(Physics::getPosition));
        int lastDrawingPosition = viewport.getStart() - 1;
        for (Physics physics : batch) {
            if (physics.getPosition() < viewport.getStart() || physics.getPosition() == lastDrawingPosition) {
                continue;
            } else if (physics.getPosition() >= viewport.getEnd()) {
                break;
            }
            for (int i = lastDrawingPosition + 1; i < physics.getPosition(); i++) {
                System.out.print("   ");
            }
            Health health = healthMapper.get(physics.getEntityId());
            System.out.print(health.getValue() + "/" + health.getMaximum());
            lastDrawingPosition = physics.getPosition();
        }
        System.out.println();
    }

    public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
        this.physicsMapper = physicsMapper;
    }

    public void setHealthMapper(ComponentMapper<Health> healthMapper) {
        this.healthMapper = healthMapper;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}
