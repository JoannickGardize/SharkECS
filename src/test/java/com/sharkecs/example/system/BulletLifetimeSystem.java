package com.sharkecs.example.system;

import com.sharkecs.ComponentMapper;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Bullet;
import com.sharkecs.example.singleton.Time;
import com.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@WithAll(Bullet.class)
public class BulletLifetimeSystem extends IteratingSystem {

	private ComponentMapper<Bullet> bulletMapper;

	private Time time;

	@Override
	protected void process(int entityId) {
		Bullet bullet = bulletMapper.get(entityId);
		if (time.getElapsedTime() >= bullet.getDeathTime()) {
			entityManager.remove(entityId);
		}
	}

	public void setBulletMapper(ComponentMapper<Bullet> bulletMapper) {
		this.bulletMapper = bulletMapper;
	}

	public void setTime(Time time) {
		this.time = time;
	}
}
