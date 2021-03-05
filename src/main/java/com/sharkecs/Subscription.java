package com.sharkecs;

import java.net.http.WebSocket.Listener;
import java.util.ArrayList;
import java.util.List;

import com.sharkecs.util.IntBag;

/**
 * <p>
 * Collection of entity IDs. Insertions and deletions can be listened via the
 * {@link Listener} interface. Supports fast removal by entity ID. The order of
 * the collection is unspecified.
 * <p>
 * The behavior of calling {@link #add(int)} multiple times successively with
 * the same entity ID is unspecified, same with {@link #remove(int)}.
 * <p>
 * This class is mostly used internally as {@link Aspect} subscription.
 * 
 * @author Joannick Gardize
 *
 */
public class Subscription {

    private IntBag entities;
    private IntBag entityIndexes;

    private List<SubscriptionListener> listeners;

    public Subscription(int expectedEntityCount) {
        entities = new IntBag(expectedEntityCount);
        entityIndexes = new IntBag(expectedEntityCount);
        listeners = new ArrayList<>();
    }

    public void addListener(SubscriptionListener listener) {
        listeners.add(listener);
    }

    public void add(int entityId) {
        entityIndexes.unsafeSet(entityId, entities.size());
        entities.add(entityId);
        for (SubscriptionListener listener : listeners) {
            listener.added(entityId);
        }
    }

    public void remove(int entityId) {
        int removeIndex = entityIndexes.get(entityId);
        entityIndexes.unsafeSet(entities.remove(removeIndex), removeIndex);
        for (SubscriptionListener listener : listeners) {
            listener.removed(entityId);
        }
    }

    public void notifyChanged(int entityId) {
        for (SubscriptionListener listener : listeners) {
            listener.changed(entityId);
        }
    }

    public IntBag getEntities() {
        return entities;
    }

}
