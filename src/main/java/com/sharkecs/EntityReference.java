package com.sharkecs;

import java.util.function.IntConsumer;

/**
 * <p>
 * Wrapper of entity ID used to safely reference entities. Created by calling
 * {@link EntityManager#reference(int)}.
 * <p>
 * Become in an <i>empty</i> state once the entity doesn't exists anymore, at
 * this point, the stored entity ID become -1.
 * 
 * @author Joannick Gardize
 *
 */
public class EntityReference {

	private static final EntityReference EMPTY = new EntityReference(-1);

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

	/**
	 * Executes the given action if the referenced entity still exists. the int
	 * parameter will be the entity id.
	 * 
	 * @param action the action to execute if the entity still exists.
	 */
	public void ifExists(IntConsumer action) {
		if (exists()) {
			action.accept(id);
		}
	}

	/**
	 * Returns an empty entity reference. Useful to initialize fields and keep them
	 * null-safe.
	 * 
	 * @return an EntityReference instance referencing to nothing
	 */
	public static EntityReference empty() {
		return EMPTY;
	}

	void clear() {
		id = -1;
	}
}
