package com.sharkecs.example.system;

import com.sharkecs.Archetype;
import com.sharkecs.ComponentMapper;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Bullet;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.component.Shooter;
import com.sharkecs.example.component.Shooter.ShooterCommand;
import com.sharkecs.example.singleton.Time;
import com.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@WithAll({ Shooter.class, Physics.class })
public class ShootSystem extends IteratingSystem {

	private ComponentMapper<Shooter> shooterMapper;
	private ComponentMapper<Physics> physicsMapper;
	private ComponentMapper<Bullet> bulletMapper;

	private Time time;

	private Archetype bullet;

	@Override
	protected void process(int entityId) {
		Shooter shooter = shooterMapper.get(entityId);
		if (time.getElapsedTime() >= shooter.getReadyTime()) {
			if (shooter.getCommand() == ShooterCommand.RIGHT) {
				shoot(shooter, entityId, 1);
			} else if (shooter.getCommand() == ShooterCommand.LEFT) {
				shoot(shooter, entityId, -1);
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

	public void setTime(Time time) {
		this.time = time;
	}

	public void setBullet(Archetype bullet) {
		this.bullet = bullet;
	}
}
