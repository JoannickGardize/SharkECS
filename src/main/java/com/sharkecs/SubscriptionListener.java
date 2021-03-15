package com.sharkecs;

/**
 * Listener interface for {@link Subscription}s.
 * 
 * @author Joannick Gardize
 *
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
	 * Called when an entity transmuted, and as a result the entity is still
	 * subscribed to the subscription.
	 * 
	 * @param entityId the entity for which the component composition has changed
	 */
	void changed(int entityId);
}
