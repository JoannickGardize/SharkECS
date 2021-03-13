package com.sharkecs;

import com.sharkecs.annotation.SkipInjection;
import com.sharkecs.util.Bag;
import com.sharkecs.util.IntBag;

@SkipInjection
public class EntityManager implements Processor {

	private static class InsertionEntry {
		int id;
		Archetype archetype;
	}

	private static class TransmutationEntry {
		int id;
		Transmutation transmutation;
	}

	private Bag<Archetype> entities;
	private IntBag recycleBin;
	private IntBag pendingRemoval;
	private Bag<InsertionEntry> pendingInsertion;
	private Bag<TransmutationEntry> pendingTransmutation;
	private int nextId;

	public EntityManager(int expectedEntityCount) {
		entities = new Bag<>(expectedEntityCount);
		int tmpCollectionsSize = expectedEntityCount / 10;
		recycleBin = new IntBag(tmpCollectionsSize);
		pendingInsertion = new Bag<>(tmpCollectionsSize);
		pendingRemoval = new IntBag(tmpCollectionsSize);
		pendingTransmutation = new Bag<>(tmpCollectionsSize);
		nextId = 0;
	}

	/**
	 * Creates a new entity for the given archetype. All components are immediately
	 * created, but the insertion will be effective for the next process cycle.
	 * 
	 * @param archetype the archetype of the new entity, components will be created
	 *                  accordingly
	 * @return the new entity id
	 */
	public int create(Archetype archetype) {
		int id = recycleBin.isEmpty() ? nextId++ : recycleBin.removeLast();
		InsertionEntry entry = pendingInsertion.nextOrAdd(InsertionEntry::new);
		entry.id = id;
		entry.archetype = archetype;
		for (ComponentMapper<Object> mapper : archetype.getAutoCreateComponentMappers()) {
			mapper.create(id);
		}
		return id;
	}

	/**
	 * Remove the given entity for the next process cycle.
	 * 
	 * @param entityId
	 */
	public void remove(int entityId) {
		pendingRemoval.add(entityId);
	}

	/**
	 * Transmute the given entity into the given archetype. The new components are
	 * immediately created. The effective transmutation is done for the next process
	 * cycle.
	 * 
	 * @param entityId
	 * @param toArchetype
	 */
	public void transmute(int entityId, Archetype toArchetype) {
		Archetype archetype = entities.get(entityId);
		Transmutation transmutation = archetype.getTransmutations()[toArchetype.getId()];
		for (ComponentMapper<Object> mapper : transmutation.getAddMappers()) {
			mapper.create(entityId);
		}
		TransmutationEntry entry = pendingTransmutation.nextOrAdd(TransmutationEntry::new);
		entry.id = entityId;
		entry.transmutation = transmutation;
	}

	@Override
	public void process() {
		insertPending();
		transmutePending();
		removePending();
		nextId = entities.size();
	}

	public Archetype archetypeOf(int entityId) {
		return entities.get(entityId);
	}

	private void insertPending() {
		for (int i = 0, size = pendingInsertion.size(); i < size; i++) {
			InsertionEntry entry = pendingInsertion.get(i);
			entities.set(entry.id, entry.archetype);
			for (Subscription subscription : entry.archetype.getSubscriptions()) {
				subscription.add(entry.id);
			}
		}
		pendingInsertion.clear();
	}

	private void removePending() {
		for (int i = 0, size = pendingRemoval.size(); i < size; i++) {
			int entityId = pendingRemoval.get(i);
			Archetype archetype = entities.get(entityId);
			if (archetype != null) {
				entities.unsafeSet(entityId, null);
				recycleBin.add(entityId);
				for (Subscription subscription : archetype.getSubscriptions()) {
					subscription.remove(entityId);
				}
				for (ComponentMapper<Object> mapper : archetype.getComponentMappers()) {
					mapper.remove(entityId);
				}
			}
		}
		pendingRemoval.clear();
	}

	private void transmutePending() {
		for (int i = 0, size = pendingTransmutation.size(); i < size; i++) {
			TransmutationEntry entry = pendingTransmutation.get(i);
			int id = entry.id;
			Transmutation transmutation = entry.transmutation;
			for (Subscription subscription : transmutation.getAddSubscriptions()) {
				subscription.add(id);
			}
			for (Subscription subscription : transmutation.getChangeSubscriptions()) {
				subscription.notifyChanged(id);
			}
			for (Subscription subscription : transmutation.getRemoveSubscriptions()) {
				subscription.remove(id);
			}
			for (ComponentMapper<Object> mapper : transmutation.getRemoveMappers()) {
				mapper.remove(id);
			}
		}
		pendingTransmutation.clear();
	}
}
