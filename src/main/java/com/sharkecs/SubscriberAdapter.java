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

package com.sharkecs;

import com.sharkecs.annotation.RequiresEntityTracking;
import com.sharkecs.annotation.SkipInject;
import com.sharkecs.util.IntBag;

import java.util.Objects;

/**
 * Base implementation of a {@link Subscriber}. Listen the {@link Subscription}
 * and make the listening methods optional.
 *
 * @author Joannick Gardize
 */
@SkipInject
public abstract class SubscriberAdapter implements Subscriber {

    private Subscription subscription;

    @Override
    public void subscribe(Subscription subscription) {
        Objects.requireNonNull(subscription);
        if (this.subscription != null) {
            throw new IllegalStateException("The subscriber already has a subscription");
        }
        this.subscription = subscription;
        this.subscription.addListener(this);
    }

    /**
     * @return the entity IDs of the subscription. must be subscribed first. The
     * returned collection must not be modified.
     * @throws UnsupportedOperationException if the subscription does not maintain
     *                                       the entity collection (see
     *                                       {@link RequiresEntityTracking})
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
    public void changed(int entityId, Transmutation transmutation) {
        // Nothing by default, not always required.
    }
}
