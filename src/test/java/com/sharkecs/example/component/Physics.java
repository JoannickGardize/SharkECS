package com.sharkecs.example.component;

import java.util.List;

public class Physics {

	private int position;
	private int speed;
	private int entityId = -1;
	private List<Physics> collisionGroup;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public List<Physics> getCollisionGroup() {
		return collisionGroup;
	}

	public void setCollisionGroup(List<Physics> collisionGroup) {
		this.collisionGroup = collisionGroup;
	}
}
