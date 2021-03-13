package com.sharkecs;

import java.util.ArrayList;
import java.util.List;

import com.sharkecs.util.IntBag;

/**
 * <p>
 * Subscription of a given group of entity, generally of a given {@link Aspect}.
 * Notify insertion, removal, and change.
 * <p>
 * Does not track the actual collection of entity. Use
 * {@link TrackingSubscription} for that.
 * 
 * @author Joannick Gardize
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
	public void notifyChanged(int entityId) {
		for (SubscriptionListener listener : listeners) {
			listener.changed(entityId);
		}
	}

	/**
	 * <p>
	 * Get the maintained collection of entities this subscription is interested of.
	 * The returned bag is intended to only be read and modifying it may result to
	 * unexpected behaviors.
	 * 
	 * <p>
	 * Not supported by this class, use {@link TrackingSubscription} for that.
	 * 
	 * @return the maintained collection of entities this subscription is interested
	 *         of
	 */
	public IntBag getEntities() {
		throw new UnsupportedOperationException();
	}
}
