package com.sharkecs;

import java.util.ArrayList;
import java.util.List;

import com.sharkecs.annotation.SkipInject;
import com.sharkecs.util.IntBag;

/**
 * <p>
 * Subscription of a given group of entity, generally of a given {@link Aspect}.
 * Notify insertion, removal, and change.
 * <p>
 * Does not track the actual collection of entity, the
 * {@link TrackingSubscription} specialization is used for that.
 * 
 * @author Joannick Gardize
 *
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
	 *         of
	 */
	public IntBag getEntities() {
		throw new UnsupportedOperationException();
	}
}
