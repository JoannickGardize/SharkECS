package com.sharkecs;

import com.sharkecs.util.IntBag;

/**
 * The most common base class for an entity iterating system. Subscribe to an
 * entity group via an {@link Aspect}, and process each entity of the
 * subscription one time per {@link #process()}.
 * 
 * @author Joannick Gardize
 *
 */
public abstract class IteratingSystem extends SubscriberAdapter implements Processor, Initializable {

	@Override
	public final void process() {
		beginProcess();
		IntBag entities = getEntities();
		for (int i = 0, size = entities.size(); i < size; i++) {
			process(entities.get(i));
		}
		endProcess();
	}

	@Override
	public void initialize() {
		// Nothing by default
	}

	/**
	 * Called at the beginning of a process run. Does nothing by default.
	 */
	protected void beginProcess() {
		// Nothing by default
	}

	/**
	 * Process the given entity
	 * 
	 * @param entityId the ID of the entity to process
	 */
	protected abstract void process(int entityId);

	/**
	 * Called at the end of a process run. Does nothing by default.
	 */
	protected void endProcess() {
		// Nothing by default
	}
}
