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

package sharkhendrix.sharkecs.example.component;

import sharkhendrix.sharkecs.EntityReference;
import sharkhendrix.sharkecs.example.system.PhysicsSystem;

import java.util.List;

/**
 * Component representing the physical representation of an entity, it's a
 * "point body", able to move, in a one-dimensional, integer axis world.
 */
public class Physics {

    private int position;
    private int speed;
    private List<EntityReference> collisionGroup;

    /**
     * @return the actual position of this physics
     */
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the movement speed of the physics, in position unit per time unit
     */
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * @return the actual collision group of this physics (all physics at the same
     * position), maintained by the {@link PhysicsSystem}, contains at least
     * this physics
     */
    public List<EntityReference> getCollisionGroup() {
        return collisionGroup;
    }

    public void setCollisionGroup(List<EntityReference> collisionGroup) {
        this.collisionGroup = collisionGroup;
    }
}
