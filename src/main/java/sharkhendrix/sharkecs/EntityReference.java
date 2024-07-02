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

import java.util.function.IntConsumer;

/**
 * <p>
 * Wrapper of entity ID used to safely reference entities. Created by calling
 * {@link EntityManager#reference(int)}.
 * <p>
 * Become in an <i>empty</i> state once the entity doesn't exists anymore, at
 * this point, the stored entity ID become -1.
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
     * been removed
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
