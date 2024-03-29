package com.sharkecs;

import com.sharkecs.annotation.SkipInject;
import com.sharkecs.util.IntBag;

/**
 * A subscription tracking the actual collection of entities. The ordering of
 * entities is arbitrary and may change other time.
 * 
 * @author Joannick Gardize
 *
 */
@SkipInject
public class TrackingSubscription extends Subscription {

	private IntBag entities;
	private IntBag entityIndexes;

	public TrackingSubscription(int expectedEntityCount) {
		entities = new IntBag(expectedEntityCount);
		entityIndexes = new IntBag(expectedEntityCount);
	}

	@Override
	public void add(int entityId) {
		entityIndexes.put(entityId, entities.size());
		entities.add(entityId);
		super.add(entityId);
	}

	@Override
	public void remove(int entityId) {
		int removeIndex = entityIndexes.get(entityId);
		entityIndexes.unsafeSet(entities.remove(removeIndex), removeIndex);
		super.remove(entityId);
	}

	@Override
	public IntBag getEntities() {
		return entities;
	}
}
