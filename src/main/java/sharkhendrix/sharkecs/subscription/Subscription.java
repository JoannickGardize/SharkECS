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

package sharkhendrix.sharkecs.subscription;

import sharkhendrix.sharkecs.Archetype;
import sharkhendrix.sharkecs.Aspect;
import sharkhendrix.sharkecs.Transmutation;
import sharkhendrix.sharkecs.util.IntIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Subscription of a given group of entity, generally of a given {@link Aspect}.
 * Notify insertion, removal, and change.
 * <p>
 * Does not track the actual collection of entity, the
 * {@link TrackingSubscription} or {@link SortedTrackingSubscription} are used for that.
 *
 * @see TrackingSubscription
 */
public class Subscription {

    private List<SubscriptionListener> listeners = new ArrayList<>();

    public void addListener(SubscriptionListener listener) {
        listeners.add(listener);
    }

    /**
     * Notify listeners that the given entity has been added to this subscription.
     *
     * @param entity
     */
    public void add(int entity) {
        for (SubscriptionListener listener : listeners) {
            listener.added(entity);
        }
    }

    /**
     * Notify listeners that the given entity has been removed from this
     * subscription.
     *
     * @param entity
     */
    public void remove(int entity) {
        for (SubscriptionListener listener : listeners) {
            listener.removed(entity);
        }
    }

    /**
     * Notify listeners that the given entity has changed its {@link Archetype}, but
     * is kept to this subscription.
     *
     * @param entity
     */
    public void notifyChanged(int entity, Transmutation transmutation) {
        for (SubscriptionListener listener : listeners) {
            listener.changed(entity, transmutation);
        }
    }

    /**
     * <p>
     * This method is only supported by {@link TrackingSubscription} instances.
     *
     * @return
     */
    public IntIterator iterator() {
        throw new UnsupportedOperationException();
    }
}
