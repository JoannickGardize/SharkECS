package com.sharkecs.example.system;

import com.sharkecs.ComponentMapper;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Bullet;
import com.sharkecs.example.component.Health;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@WithAll({ Physics.class, Bullet.class })
public class BulletDamageSystem extends IteratingSystem {

	private ComponentMapper<Physics> physicsMapper;
	private ComponentMapper<Bullet> bulletMapper;
	private ComponentMapper<Health> healthMapper;

	@Override
	public void process(int entityId) {
		Physics physics = physicsMapper.get(entityId);
		for (Physics colliding : physics.getCollisionGroup()) {
			int collidingId = colliding.getEntityId();
			healthMapper.ifExists(collidingId, health -> {
				Bullet bullet = bulletMapper.get(entityId);
				health.takeDamage(bullet.getDamage());
				entityManager.remove(entityId);
			});
		}
	}

	public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
		this.physicsMapper = physicsMapper;
	}

	public void setBulletMapper(ComponentMapper<Bullet> bulletMapper) {
		this.bulletMapper = bulletMapper;
	}

	public void setHealthMapper(ComponentMapper<Health> healthMapper) {
		this.healthMapper = healthMapper;
	}
}
