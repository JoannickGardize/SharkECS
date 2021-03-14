package com.sharkecs;

/**
 * Convenience class to extends {@link SubscriberAdapter}, implements
 * {@link Processor} and {@link Initializable}, which is the most common base
 * for an entity processing system.
 * 
 * @author Joannick Gardize
 *
 */
public abstract class EntitySystem extends SubscriberAdapter implements Processor, Initializable {

	@Override
	public void initialize() {
		// Nothing by default
	}
}
