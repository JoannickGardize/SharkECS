package com.sharkecs;

/**
 * Wrapper of entity ID used to safely reference entities. Created by calling
 * {@link EntityManager#reference(int)}.
 * 
 * @author Joannick Gardize
 *
 */
public class EntityReference {

	private int id;

	EntityReference(int id) {
		this.id = id;
	}

	/**
	 * @return the referenced entity id, or -1 if the entity has been removed
	 */
	public int get() {
		return id;
	}

	/**
	 * @return true if the referenced entity still exists, false if the entity has
	 *         been removed
	 */
	public boolean exists() {
		return id != -1;
	}

	void clear() {
		id = -1;
	}
}
