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
import sharkhendrix.sharkecs.IteratingSystem;
import sharkhendrix.sharkecs.annotation.SortEntities;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.example.component.Health;
import sharkhendrix.sharkecs.example.component.Physics;
import sharkhendrix.sharkecs.example.misc.Viewport;
import sharkhendrix.sharkecs.example.system.annotation.DrawingPhase;

@DrawingPhase
@SortEntities("position")
@With({Physics.class, Health.class})
public class HealthDrawerSystem extends IteratingSystem {

    private ComponentMapper<Physics> physicsMapper;

    private ComponentMapper<Health> healthMapper;

    private Viewport viewport;

    private int lastDrawingPosition;

    @Override
    protected void beginProcess() {
        lastDrawingPosition = viewport.getStart() - 1;
    }

    @Override
    protected void process(int entity) {
        Physics physics = physicsMapper.get(entity);

        if (physics.getPosition() < viewport.getStart()
                || physics.getPosition() == lastDrawingPosition
                || physics.getPosition() >= viewport.getEnd()) {
            return;
        }
        for (int i = lastDrawingPosition + 1; i < physics.getPosition(); i++) {
            System.out.print("   ");
        }
        Health health = healthMapper.get(entity);
        System.out.print(health.getValue() + "/" + health.getMaximum());
        lastDrawingPosition = physics.getPosition();
    }

    @Override
    protected void endProcess() {
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
