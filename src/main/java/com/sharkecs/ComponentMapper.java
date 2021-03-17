package com.sharkecs;

import java.util.function.Consumer;

/**
 * Base interface for component mappers. A component mapper is responsible of
 * binding all components of a given type to entities.
 * 
 * @author Joannick Gardize
 *
 * @param <T> the component type this mapper is responsible of
 */
public interface ComponentMapper<T> {

	/**
	 * Create a component for the given entity. The implementation may use recycled
	 * instances of components.
	 * 
	 * @param entityId the entity id associated to the newly created (or recycled)
	 *                 component
	 * @return the newly created or recycled component associated to the entity
	 */
	T create(int entityId);

	/**
	 * Associates the given component to the given entity.
	 * 
	 * @param entityId
	 * @param component
	 */
	void put(int entityId, T component);

	/**
	 * Remove the component associated to the given entity. The behavior of trying
	 * to remove a non-existing component is undefined.
	 * 
	 * @param entityId the entity for which the component will be removed
	 */
	void remove(int entityId);

	/**
	 * <p>
	 * Retrieve the component associated to the given entity. The component must
	 * exists, or the behavior of this method is undefined.
	 * <p>
	 * Prefer {@link #getIfExists(int)} if you are not sure the component does
	 * exists.
	 * 
	 * @param entityId the entity id for which the associated component will be
	 *                 returned
	 * @return the component associated to the given entity.
	 */
	T get(int entityId);

	/**
	 * Retrieve the component associated to the given entity, or null if the given
	 * entity doesn't hold this component type.
	 * 
	 * @param entityId the entity id for which the associated component will be
	 *                 returned
	 * @return the component associated to the given entity, or null if it doesn't
	 *         exists.
	 */
	T getIfExists(int entityId);

	/**
	 * Execute the given {@code action} if the given entity has a component from
	 * this mapper. The default implementation uses {@link #getIfExists(int)}.
	 * 
	 * @param entityId
	 * @param action
	 */
	default void ifExists(int entityId, Consumer<T> action) {
		T component = getIfExists(entityId);
		if (component != null) {
			action.accept(component);
		}
	}

	/**
	 * Test if the given entity has this type of component. The default
	 * implementation uses {@link #getIfExists(int)}.
	 * 
	 * @param entityId
	 * @return true if the entity has this type of component, false otherwise
	 */
	default boolean has(int entityId) {
		return getIfExists(entityId) != null;
	}
}
