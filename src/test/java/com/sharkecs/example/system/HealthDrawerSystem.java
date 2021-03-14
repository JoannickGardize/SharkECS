package com.sharkecs.example.system;

import java.util.ArrayList;
import java.util.List;

import com.sharkecs.ComponentMapper;
import com.sharkecs.IteratingSystem;
import com.sharkecs.annotation.WithAll;
import com.sharkecs.example.component.Health;
import com.sharkecs.example.component.Physics;
import com.sharkecs.example.singleton.Viewport;

@WithAll({ Physics.class, Health.class })
public class HealthDrawerSystem extends IteratingSystem {

	private ComponentMapper<Physics> physicsMapper;

	private ComponentMapper<Health> healthMapper;

	private List<Physics> batch = new ArrayList<>();

	private Viewport viewport;

	@Override
	protected void beginProcess() {
		batch.clear();
	}

	@Override
	protected void process(int entityId) {
		batch.add(physicsMapper.get(entityId));
	}

	@Override
	protected void endProcess() {
		batch.sort((e1, e2) -> Integer.compare(e1.getPosition(), e2.getPosition()));
		int lastDrawingPosition = viewport.getStart() - 1;
		for (Physics physics : batch) {
			if (physics.getPosition() < viewport.getStart() || physics.getPosition() == lastDrawingPosition) {
				continue;
			} else if (physics.getPosition() >= viewport.getEnd()) {
				break;
			}
			for (int i = lastDrawingPosition + 1; i < physics.getPosition(); i++) {
				System.out.print("   ");
			}
			Health health = healthMapper.get(physics.getEntityId());
			System.out.print(health.getValue() + "/" + health.getMaximum());
			lastDrawingPosition = physics.getPosition();
		}
		System.out.println();
	}

	public void setPhysicsMapper(ComponentMapper<Physics> physicsMapper) {
		this.physicsMapper = physicsMapper;
	}

	public void setHealthMapper(ComponentMapper<Health> healthMapper) {
		this.healthMapper = healthMapper;
	}

	public void setBatch(List<Physics> batch) {
		this.batch = batch;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
}
