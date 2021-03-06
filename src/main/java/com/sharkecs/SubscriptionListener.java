package com.sharkecs;

/**
 * Listener interface for {@link Subscription}s.
 * 
 * @author Joannick Gardize
 *
 */
public interface SubscriptionListener {

	void added(int entityId);

	void removed(int entityId);

	void changed(int entityId);
}
