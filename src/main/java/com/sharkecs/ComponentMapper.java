package com.sharkecs;

/**
 * Base interface for component mappers. A component mapper is responsible of
 * binding all components of a given type to entities.
 * 
 * @author Joannick Gardize
 *
 * @param <T>
 */
public interface ComponentMapper<T> {

	/**
	 * Create a component for the given entity. The implementation may use recycled
	 * instances of components.
	 * 
	 * @param entityId the entity id associated to the newly created (or recycled)
	 *                 component
	 * @return the newly created component
	 */
	T create(int entityId);

	/**
	 * Associates the given component to the given entity
	 * 
	 * @param entityId
	 * @param component
	 */
	void put(int entityId, T component);

	/**
	 * Remove the component associated with the given entity. The behavior of trying
	 * to remove a non-existing component is undefined.
	 * 
	 * @param entityId the entity for which the component will be removed
	 */
	void remove(int entityId);

	/**
	 * Retrieve the component associated to the given entity. The component must
	 * exists, or the behavior of this method is undefined.
	 * 
	 * @param entityId the entity id for which the associated component will be
	 *                 returned
	 * @return the component associated to the given entity.
	 */
	T get(int entityId);

	/**
	 * Retrieve the component associated to the given entity, or null if the given
	 * entity doesn't have this component.
	 * 
	 * @param entityId the entity id for which the associated component will be
	 *                 returned
	 * @return the component associated to the given entity, or null if doesn't
	 *         exists.
	 */
	T getIfExists(int entityId);

	/**
	 * Test if the given entity has this type of component.
	 * 
	 * @param entityId
	 * @return true if the entity has this type of component, false otherwise
	 */
	default boolean has(int entityId) {
		return getIfExists(entityId) != null;
	}
}
