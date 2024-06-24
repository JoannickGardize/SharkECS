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

import com.sharkecs.annotation.SkipInject;
import com.sharkecs.util.IntBag;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Subscription of a given group of entity, generally of a given {@link Aspect}.
 * Notify insertion, removal, and change.
 * <p>
 * Does not track the actual collection of entity, the
 * {@link TrackingSubscription} specialization is used for that.
 *
 * @author Joannick Gardize
 * @see TrackingSubscription
 */
@SkipInject
public class Subscription {

    private List<SubscriptionListener> listeners = new ArrayList<>();

    public void addListener(SubscriptionListener listener) {
        listeners.add(listener);
    }

    /**
     * Notify listeners that the given entity has been added to this subscription.
     *
     * @param entityId
     */
    public void add(int entityId) {
        for (SubscriptionListener listener : listeners) {
            listener.added(entityId);
        }
    }

    /**
     * Notify listeners that the given entity has been removed from this
     * subscription.
     *
     * @param entityId
     */
    public void remove(int entityId) {
        for (SubscriptionListener listener : listeners) {
            listener.removed(entityId);
        }
    }

    /**
     * Notify listeners that the given entity has changed its {@link Archetype}, but
     * is kept to this subscription.
     *
     * @param entityId
     */
    public void notifyChanged(int entityId, Transmutation transmutation) {
        for (SubscriptionListener listener : listeners) {
            listener.changed(entityId, transmutation);
        }
    }

    /**
     * <p>
     * Get the maintained collection of entities this subscription is interested of.
     * The returned bag is intended to only be read, modifying it may result to
     * unexpected behaviors.
     *
     * <p>
     * This method is only supported by {@link TrackingSubscription} instances.
     *
     * @return the maintained collection of entities this subscription is interested
     * of
     */
    public IntBag getEntities() {
        throw new UnsupportedOperationException();
    }
}
