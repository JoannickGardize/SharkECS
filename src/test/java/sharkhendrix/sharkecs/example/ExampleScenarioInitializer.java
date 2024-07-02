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

import sharkhendrix.sharkecs.Archetype;
import sharkhendrix.sharkecs.ComponentMapper;
import sharkhendrix.sharkecs.EntityManager;
import sharkhendrix.sharkecs.Initializable;
import sharkhendrix.sharkecs.example.component.Health;
import sharkhendrix.sharkecs.example.component.Image;
import sharkhendrix.sharkecs.example.component.Physics;
import sharkhendrix.sharkecs.example.component.Shooter;
import sharkhendrix.sharkecs.example.misc.Images;
import sharkhendrix.sharkecs.example.misc.Viewport;

public class ExampleScenarioInitializer implements Initializable {

    private EntityManager entityManager;

    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Health> healthMapper;
    private ComponentMapper<Shooter> shooterMapper;
    private ComponentMapper<Image> imageMapper;

    private Archetype player;

    private Viewport viewport;

    @Override
    public void initialize() {
        viewport.setStart(0);
        viewport.setEnd(16);
        createPlayer(1, Shooter.ShooterCommand.RIGHT);
        createPlayer(8, Shooter.ShooterCommand.RIGHT);
        createPlayer(14, Shooter.ShooterCommand.LEFT);
    }

    private void createPlayer(int position, Shooter.ShooterCommand initialCommand) {
        int entity = entityManager.create(player);
        physicsMapper.create(entity).setPosition(position);
        healthMapper.create(entity).initialize(2);
        Shooter shooter = shooterMapper.create(entity);
        shooter.setCooldown(4);
        shooter.setCommand(initialCommand);
        imageMapper.create(entity).setImage(initialCommand == Shooter.ShooterCommand.RIGHT ?
                Images.PLAYER_RIGHT : Images.PLAYER_LEFT);
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

    public void setImageMapper(ComponentMapper<Image> imageMapper) {
        this.imageMapper = imageMapper;
    }

    public void setPlayer(Archetype player) {
        this.player = player;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}
