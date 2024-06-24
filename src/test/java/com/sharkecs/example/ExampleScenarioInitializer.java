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

import com.sharkecs.Archetype;
import com.sharkecs.ComponentMapper;
import com.sharkecs.EntityManager;
import com.sharkecs.Initializable;
import com.sharkecs.example.component.Health;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.component.Shooter;
import com.sharkecs.example.component.Shooter.ShooterCommand;
import com.sharkecs.example.singleton.Viewport;

public class ExampleScenarioInitializer implements Initializable {

    private EntityManager entityManager;

    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Health> healthMapper;
    private ComponentMapper<Shooter> shooterMapper;

    private Archetype player;

    private Viewport viewport;

    @Override
    public void initialize() {
        viewport.setStart(0);
        viewport.setEnd(16);
        createPlayer(1, ShooterCommand.RIGHT);
        createPlayer(8, ShooterCommand.RIGHT);
        createPlayer(14, ShooterCommand.LEFT);
    }

    private void createPlayer(int position, ShooterCommand initialCommand) {
        int entityId = entityManager.create(player);
        physicsMapper.create(entityId).setPosition(position);
        healthMapper.create(entityId).initialize(2);
        Shooter shooter = shooterMapper.create(entityId);
        shooter.setCooldown(4);
        shooter.setCommand(initialCommand);
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
        this.physicsMapper = physicsMapper;
    }

    public void setHealthMapper(ComponentMapper<Health> healthMapper) {
        this.healthMapper = healthMapper;
    }

    public void setShooterMapper(ComponentMapper<Shooter> shooterMapper) {
        this.shooterMapper = shooterMapper;
    }

    public void setPlayer(Archetype player) {
        this.player = player;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}
