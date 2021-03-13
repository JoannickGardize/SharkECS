package com.sharkecs.example;

import com.sharkecs.ComponentMapper;
import com.sharkecs.EntityManager;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;

@WithAll({ Physics.class, Bullet.class })
public class BulletDamageSystem extends IteratingSystem {

	private EntityManager entityManager;

	private ComponentMapper<Physics> physicsMapper;

	private ComponentMapper<Bullet> bulletMapper;

	private ComponentMapper<Health> healthMapper;

	@Override
	public void process(int entityId) {
		Physics physics = physicsMapper.get(entityId);
		for (Physics colliding : physics.getColliding()) {
			int collidingId = colliding.getEntityId();
			Health health = healthMapper.getIfExists(collidingId);
			if (health != null) {
				Bullet bullet = bulletMapper.get(entityId);
				health.takeDamage(bullet.getDamage());
				entityManager.remove(entityId);
			}
		}
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
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
