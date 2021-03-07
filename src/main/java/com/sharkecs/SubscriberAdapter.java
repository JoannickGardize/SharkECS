package com.sharkecs;

import com.sharkecs.util.IntBag;

/**
 * Base implementation of a {@link Subscriber}. Listen the {@link Subscription}
 * and make the listening methods optional.
 * 
 * @author Joannick Gardize
 *
 */
public abstract class SubscriberAdapter implements Subscriber {

	private Subscription subscription;

	@Override
	public void subscribe(Subscription subscription) {
		if (this.subscription != null) {
			throw new IllegalStateException("The subscriber already has a subscription");
		}
		this.subscription = subscription;
		this.subscription.addListener(this);
	}

	/**
	 * @return the entity IDs of the subscription. must be subscribed first.
	 */
	public IntBag getEntities() {
		return subscription.getEntities();
	}

	public Subscription getSubscription() {
		return subscription;
	}

	@Override
	public void added(int entityId) {
		// Nothing by default, not always required.
	}

	@Override
	public void removed(int entityId) {
		// Nothing by default, not always required.
	}

	@Override
	public void changed(int entityId) {
		// Nothing by default, not always required.
	}
}
