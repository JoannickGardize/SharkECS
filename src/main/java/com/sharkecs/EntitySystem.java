package com.sharkecs;

import com.sharkecs.annotation.ForceInject;

/**
 * Convenience class to extends {@link SubscriberAdapter}, implements
 * {@link Processor} and {@link Initializable}, and provides an
 * {@link EntityManager} attribute. Which is the most common base for an entity
 * processing system.
 * 
 * @author Joannick Gardize
 *
 */
public abstract class EntitySystem extends SubscriberAdapter implements Processor, Initializable {

	@ForceInject
	protected EntityManager entityManager;

	@Override
	public void initialize() {
		// Nothing by default
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
