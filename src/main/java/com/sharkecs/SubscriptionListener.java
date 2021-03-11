package com.sharkecs;

/**
 * Listener interface for {@link Subscription}s.
 * 
 * @author Joannick Gardize
 *
 */
public interface SubscriptionListener {

	/**
	 * Called when an entity has been added to this subscription.
	 * 
	 * @param entityId the newly added entity
	 */
	void added(int entityId);

	/**
	 * Called when an entity has been removed from this subscription.
	 * 
	 * @param entityId the removed entity
	 */
	void removed(int entityId);

	/**
	 * Called when an entity composition has changed, and as a result the entity is
	 * still subscribed to the subscription.
	 * 
	 * @param entityId for which the component composition has changed
	 */
	void changed(int entityId);
}
