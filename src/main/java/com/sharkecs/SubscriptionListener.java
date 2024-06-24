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

package com.sharkecs;

/**
 * Listener interface for {@link Subscription}s.
 *
 * @author Joannick Gardize
 */
public interface SubscriptionListener {

    /**
     * Called when an entity has been added to the subscription.
     *
     * @param entityId the newly added entity
     */
    void added(int entityId);

    /**
     * Called when an entity has been removed from the subscription.
     *
     * @param entityId the removed entity
     */
    void removed(int entityId);

    /**
     * Called when an entity has transmuted, and as a result the entity is still
     * subscribed to the subscription.
     *
     * @param entityId      the entity for which the component composition has
     *                      changed
     * @param transmutation the transmutation that occurred to the entity
     */
    void changed(int entityId, Transmutation transmutation);
}
