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
	 */
	void create(int entityId);

	/**
	 * Remove the component associated with the given entity, if any.
	 * 
	 * @param entityId the entity for which the component will be removed
	 */
	void remove(int entityId);

	/**
	 * Retrieve the component associated to the given entity. The behavior of trying
	 * to retrieve a component that does not exists is undefined.
	 * 
	 * @param entityId the entity id for which the associated component will be
	 *                 returned
	 * @return the component associated to the given entity
	 */
	T get(int entityId);
}
