package com.sharkecs.example.component;

import java.util.List;

import com.sharkecs.example.system.PhysicsSystem;

/**
 * Component representing the physical representation of an entity, it's a
 * "point body", able to move, in a one-dimensional, integer axis world.
 * 
 * @author Joannick gardize
 *
 */
public class Physics {

	private int position;
	private int speed;
	private int entityId = -1;
	private List<Physics> collisionGroup;

	/**
	 * @return the actual position of this physics
	 */
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the movement speed of the physics, in position unit per time unit
	 */
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * @return the entity id this physics possessing this physics
	 */
	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the actual collision group of this physics (all physics at the same
	 *         position), maintained by the {@link PhysicsSystem}, contains at least
	 *         this physics
	 */
	public List<Physics> getCollisionGroup() {
		return collisionGroup;
	}

	public void setCollisionGroup(List<Physics> collisionGroup) {
		this.collisionGroup = collisionGroup;
	}
}
