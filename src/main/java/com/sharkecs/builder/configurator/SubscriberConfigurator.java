package com.sharkecs.builder.configurator;

import com.sharkecs.Aspect;
import com.sharkecs.Subscriber;
import com.sharkecs.Subscription;
import com.sharkecs.builder.EngineBuilder;

/**
 * {@link Configurator} of {@link Subscriber}s, creates and bind
 * {@link Subscription}s via the annotation-declared aspect of the subscriber
 * type.
 * 
 * @author Joannick Gardize
 *
 */
public class SubscriberConfigurator extends TypeConfigurator<Subscriber> {

	public SubscriberConfigurator() {
		super(Subscriber.class);
	}

	@Override
	protected void configure(Subscriber subscriber, EngineBuilder engineBuilder) {
		subscriber.subscribe(engineBuilder.getRegistrations().computeIfAbsent(Subscription.class, new Aspect(subscriber.getClass()),
				() -> new Subscription(engineBuilder.getExpectedEntityCount())));
	}

}
