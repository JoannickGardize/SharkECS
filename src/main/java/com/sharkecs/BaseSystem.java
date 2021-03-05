package com.sharkecs;

public abstract class BaseSystem implements SubscriptionListener {

    private Subscription subscription;

    void setSubscription(Subscription subscription) {
        if (subscription == null) {
            throw new IllegalStateException("The system is already bound with an EntityBag");
        }
        this.subscription = subscription;
        this.subscription.addListener(this);
    }
}
