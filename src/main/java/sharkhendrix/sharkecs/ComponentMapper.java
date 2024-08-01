/*
 * Copyright 2024 Joannick Gardize
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sharkhendrix.sharkecs;

import java.util.function.Consumer;

/**
 * Base interface for component mappers. A component mapper is responsible for
 * binding all components of a given type to entities.
 *
 * @param <T> the component type this mapper is responsible for
 */
public interface ComponentMapper<T> {

    /**
     * Create a component for the given entity. The implementation may use recycled
     * instances of components.
     *
     * @param entity the entity id associated to the newly created (or recycled)
     *               component
     * @return the newly created or recycled component associated to the entity
     */
    T create(int entity);

    /**
     * Associates the given component to the given entity.
     * <p>
     * If there is already a component of this type associated to the entity, it will be replaced.
     *
     * @param entity    the entity that will be associated to the component
     * @param component the component to associate to the entity
     */
    void put(int entity, T component);

    /**
     * Remove the component associated to the given entity. The behavior of trying
     * to remove a non-existing component is undefined.
     *
     * @param entity the entity for which the component will be removed
     */
    void remove(int entity);

    /**
     * <p>
     * Retrieve the component associated to the given entity. The component must
     * exists, or the behavior of this method is undefined.
     * <p>
     * Prefer {@link #getIfExists(int)} if you are not sure the component does
     * exists.
     *
     * @param entity the entity id for which the associated component will be
     *               returned
     * @return the component associated to the given entity.
     */
    T get(int entity);

    /**
     * Retrieve the component associated to the given entity, or null if the given
     * entity doesn't hold this component type.
     *
     * @param entity the entity id for which the associated component will be
     *               returned
     * @return the component associated to the given entity, or null if it doesn't
     * exists.
     */
    T getIfExists(int entity);

    /**
     * Execute the given {@code action} if the given entity has a component from
     * this mapper. The default implementation uses {@link #getIfExists(int)}.
     *
     * @param entity
     * @param action
     */
    default void ifExists(int entity, Consumer<T> action) {
        T component = getIfExists(entity);
        if (component != null) {
            action.accept(component);
        }
    }

    /**
     * Test if the given entity has this type of component. The default
     * implementation uses {@link #getIfExists(int)}.
     *
     * @param entity
     * @return true if the entity has this type of component, false otherwise
     */
    default boolean has(int entity) {
        return getIfExists(entity) != null;
    }
}
