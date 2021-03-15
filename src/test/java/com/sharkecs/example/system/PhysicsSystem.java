package com.sharkecs.example.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sharkecs.ComponentMapper;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.SkipInjection;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.singleton.Time;
import com.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
@WithAll(Physics.class)
public class PhysicsSystem extends IteratingSystem {

	private ComponentMapper<Physics> physicsMapper;

	private Time time;

	@SkipInjection
	private Map<Integer, List<Physics>> collisions = new HashMap<>();

	@Override
	public void added(int entityId) {
		Physics physics = physicsMapper.get(entityId);
		physics.setEntityId(entityId);
	}

	@Override
	protected void beginProcess() {
		collisions.values().forEach(List::clear);
	}

	@Override
	protected void process(int entityId) {
		Physics physics = physicsMapper.get(entityId);

		// Update positions
		physics.setPosition(physics.getPosition() + physics.getSpeed() * time.getDeltaTime());

		// Update collisions
		List<Physics> collisionGroup = collisions.computeIfAbsent(physics.getPosition(), i -> new ArrayList<>());
		collisionGroup.add(physics);
		physics.setCollisionGroup(collisionGroup);
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
		this.physicsMapper = physicsMapper;
	}
}
