package com.sharkecs;

/**
 * A subscriber interested to be bound with a {@link Subscription}.
 * 
 * @author Joannick Gardize
 *
 */
public interface Subscriber extends SubscriptionListener {

	/**
	 * Subscribe this subscriber to the given {@link Subscription}. The
	 * implementation is responsible of calling
	 * {@link Subscription#addListener(SubscriptionListener)} if needed.
	 * 
	 * @param subscription the subscription to subscribe
	 */
	void subscribe(Subscription subscription);
}
