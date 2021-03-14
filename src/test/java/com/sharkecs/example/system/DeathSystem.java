package com.sharkecs.example.system;

import com.sharkecs.Archetype;
import com.sharkecs.ComponentMapper;
import com.sharkecs.EntityManager;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Health;
import com.sharkecs.example.component.Physics;

@WithAll({ Physics.class, Health.class })
public class DeathSystem extends IteratingSystem {

	private ComponentMapper<Health> healthMapper;
	private EntityManager entityManager;
	private Archetype corpse;

	@Override
	protected void process(int entityId) {
		if (healthMapper.get(entityId).getValue() <= 0) {
			entityManager.transmute(entityId, corpse);
		}
	}

	public void setHealthMapper(ComponentMapper<Health> healthMapper) {
		this.healthMapper = healthMapper;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void setCorpse(Archetype corpse) {
		this.corpse = corpse;
	}
}
