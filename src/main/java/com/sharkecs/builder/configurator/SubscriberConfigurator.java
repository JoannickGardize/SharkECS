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

package com.sharkecs.builder.configurator;

import com.sharkecs.Aspect;
import com.sharkecs.Subscriber;
import com.sharkecs.Subscription;
import com.sharkecs.TrackingSubscription;
import com.sharkecs.annotation.RequiresEntityTracking;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.builder.RegistrationMap;
import com.sharkecs.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 * {@link Configurator} of {@link Subscriber}s, creates and bind
 * {@link Subscription}s via the annotation-declared aspect of the subscriber
 * type.
 * <p>
 * Takes care of {@link RequiresEntityTracking} annotations to create the right
 * type of {@link Subscription}. Uses the {@link Prioritizer} to subscribe and
 * so get notified at runtime in the right order.
 *
 * @author Joannick Gardize
 */
public class SubscriberConfigurator extends TypeConfigurator<Subscriber> {

    public SubscriberConfigurator() {
        super(Subscriber.class);
    }

    private Map<Aspect, Boolean> subscriptionsToCreate = new HashMap<>();

    @Override
    protected void configure(Subscriber subscriber, EngineBuilder engineBuilder) {
        RequiresEntityTracking annotation = ReflectionUtils.getAnnotationOnSuperclass(subscriber.getClass(), RequiresEntityTracking.class);
        subscriptionsToCreate.merge(new Aspect(subscriber.getClass()), annotation == null || annotation.value(), Boolean::logicalOr);
    }

    @Override
    protected void endConfiguration(EngineBuilder engineBuilder) {
        RegistrationMap registrations = engineBuilder.getRegistrations();
        for (Entry<Aspect, Boolean> entry : subscriptionsToCreate.entrySet()) {
            registrations.put(Subscription.class, entry.getKey(),
                    Boolean.TRUE.equals(entry.getValue()) ? new TrackingSubscription(engineBuilder.getExpectedEntityCount()) : new Subscription());
        }
        List<Subscriber> subscribers = new ArrayList<>(registrations.getAllAssignableFrom(Subscriber.class));
        registrations.getOrFail(Prioritizer.class).prioritize(subscribers);
        subscribers.forEach(s -> s.subscribe(registrations.get(Subscription.class, new Aspect(s.getClass()))));
    }
}
