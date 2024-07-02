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

import sharkhendrix.sharkecs.Archetype;
import sharkhendrix.sharkecs.ComponentMapper;
import sharkhendrix.sharkecs.IteratingSystem;
import sharkhendrix.sharkecs.annotation.With;
import sharkhendrix.sharkecs.example.component.Bullet;
import sharkhendrix.sharkecs.example.component.Image;
import sharkhendrix.sharkecs.example.component.Physics;
import sharkhendrix.sharkecs.example.component.Shooter;
import sharkhendrix.sharkecs.example.misc.Images;
import sharkhendrix.sharkecs.example.misc.Time;
import sharkhendrix.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@With({Shooter.class, Physics.class})
public class ShootSystem extends IteratingSystem {

    private ComponentMapper<Shooter> shooterMapper;
    private ComponentMapper<Physics> physicsMapper;
    private ComponentMapper<Bullet> bulletMapper;
    private ComponentMapper<Image> imageMapper;

    private Time time;

    private Archetype bullet;

    @Override
    protected void process(int entity) {
        Shooter shooter = shooterMapper.get(entity);
        if (time.getElapsedTime() >= shooter.getReadyTime()) {
            if (shooter.getCommand() == Shooter.ShooterCommand.RIGHT) {
                shoot(shooter, entity, 1);
            } else if (shooter.getCommand() == Shooter.ShooterCommand.LEFT) {
                shoot(shooter, entity, -1);
            }
        }
    }

    private void shoot(Shooter shooter, int shooterId, int direction) {
        int bulletId = entityManager.create(bullet);

        // Setup bullet component
        Bullet bullet = bulletMapper.create(bulletId);
        bullet.setDamage(1);
        bullet.setDeathTime(time.getElapsedTime() + 20);

        // Setup physics component
        Physics shooterPhysics = physicsMapper.get(shooterId);
        Physics bulletPhysics = physicsMapper.create(bulletId);
        bulletPhysics.setPosition(shooterPhysics.getPosition());
        bulletPhysics.setSpeed(direction);

        // Setup image
        imageMapper.create(bulletId).setImage(direction > 0 ? Images.BULLET_RIGHT : Images.BULLET_LEFT);

        // Update cooldown
        shooter.setReadyTime(time.getElapsedTime() + shooter.getCooldown());
    }

    public void setShooterMapper(ComponentMapper<Shooter> shooterMapper) {
        this.shooterMapper = shooterMapper;
    }

    public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
        this.physicsMapper = physicsMapper;
    }

    public void setBulletMapper(ComponentMapper<Bullet> bulletMapper) {
        this.bulletMapper = bulletMapper;
    }

    public void setImageMapper(ComponentMapper<Image> imageMapper) {
        this.imageMapper = imageMapper;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setBullet(Archetype bullet) {
        this.bullet = bullet;
    }
}
