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

package sharkhendrix.sharkecs.builder.configurator;


import sharkhendrix.sharkecs.Aspect;
import sharkhendrix.sharkecs.SortableEntityListSupplier;
import sharkhendrix.sharkecs.annotation.RequiresEntityTracking;
import sharkhendrix.sharkecs.annotation.SortEntities;
import sharkhendrix.sharkecs.builder.EngineBuilder;
import sharkhendrix.sharkecs.builder.EngineConfigurationException;
import sharkhendrix.sharkecs.builder.RegistrationMap;
import sharkhendrix.sharkecs.subscription.SortedTrackingSubscription;
import sharkhendrix.sharkecs.subscription.Subscriber;
import sharkhendrix.sharkecs.subscription.Subscription;
import sharkhendrix.sharkecs.subscription.TrackingSubscription;
import sharkhendrix.sharkecs.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * {@link Configurator} of {@link Subscriber}s, creates and bind
 * {@link Subscription}s via the annotation-declared aspect of the subscriber
 * type.
 * <p>
 * For the case where multiple subscribers are interested to the same Aspect,
 * it creates as less subscription objects as possible
 * regarding {@link RequiresEntityTracking} and {@link SortEntities} annotations.
 * Also, it will create the less complex Subscription type required in that priority order:
 * {@link Subscription}, {@link  TrackingSubscription}, {@link  SortedTrackingSubscription}.
 * As a consequence, a subscriber that does not request for tracking or sorting may register to a subscription with
 * tracking or sorting, which should not affect it.
 * <p>
 * Uses the {@link Prioritizer} to subscribe and
 * so get notified at runtime in the right order.
 */
public class SubscriberConfigurator extends TypeConfigurator<Subscriber> {

    public SubscriberConfigurator() {
        super(Subscriber.class);
    }

    @Override
    protected void configure(Subscriber subscriber, EngineBuilder engineBuilder) {
        RequiresEntityTracking tracking = ReflectionUtils.getAnnotationOnSuperclass(subscriber.getClass(), RequiresEntityTracking.class);
        boolean requiresTracking = tracking == null || tracking.value();
        String sortName = getSubscriberSortName(subscriber);
        if (sortName != null && !requiresTracking) {
            throw new EngineConfigurationException("Inconsistent annotation on subscriber "
                    + subscriber.getClass().getSimpleName()
                    + ": @SortEntities requires tracking but @RequiresEntityTracking is set to false.");
        }
        Aspect aspect = new Aspect(subscriber.getClass());
        RegistrationMap registrations = engineBuilder.getRegistrations();
        SubscriptionGroup group = registrations.get(SubscriptionGroup.class, aspect);
        if (group == null) {
            group = new SubscriptionGroup();
            group.setRequiresTracking(requiresTracking);
            group.getSubscriptionsBySort().put(sortName, null);
            registrations.put(aspect, group);
        } else {
            group.setRequiresTracking(group.isRequiresTracking() || requiresTracking);
            group.getSubscriptionsBySort().put(sortName, null);
        }
    }

    @Override
    protected void endConfiguration(EngineBuilder engineBuilder) {
        RegistrationMap registrations = engineBuilder.getRegistrations();
        for (Entry<Object, SubscriptionGroup> entry : registrations.entrySet(SubscriptionGroup.class)) {
            SubscriptionGroup subscriptionGroup = entry.getValue();
            Map<String, Subscription> map = subscriptionGroup.getSubscriptionsBySort();
            if (map.size() == 1 && map.containsKey(null)) {
                map.put(null, subscriptionGroup.isRequiresTracking() ? new TrackingSubscription(engineBuilder.getExpectedEntityCount()) : new Subscription());
            } else {
                Subscription anySubscription = null;
                for (Entry<String, Subscription> sortEntry : map.entrySet()) {
                    if (sortEntry.getKey() != null) {
                        anySubscription = new SortedTrackingSubscription(
                                registrations.get(SortableEntityListSupplier.class, sortEntry.getKey()).get());
                        sortEntry.setValue(anySubscription);
                    }
                }
                map.put(null, anySubscription);
            }
        }
        List<Subscriber> subscribers = new ArrayList<>(registrations.getAllAssignableFrom(Subscriber.class));
        registrations.getOrFail(Prioritizer.class).prioritize(subscribers);
        subscribers.forEach(s -> {
            String sortName = getSubscriberSortName(s);
            SubscriptionGroup group = registrations.get(SubscriptionGroup.class, new Aspect(s.getClass()));
            s.subscribe(group.getSubscriptionsBySort().get(sortName));
        });
    }

    private String getSubscriberSortName(Subscriber subscriber) {
        SortEntities sort = ReflectionUtils.getAnnotationOnSuperclass(subscriber.getClass(), SortEntities.class);
        return sort == null ? null : sort.value();
    }
}
