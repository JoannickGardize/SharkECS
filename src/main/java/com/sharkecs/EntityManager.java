package com.sharkecs;

import com.sharkecs.Archetype.ComponentCreationPolicy;
import com.sharkecs.annotation.SkipInject;
import com.sharkecs.util.Bag;
import com.sharkecs.util.IntBag;

/**
 * <p>
 * Manage all entities. Provides entity creation, deletion, and mutation
 * operations. These three operations are delayed and effective for the next
 * process cycle.
 * <p>
 * {@link #reference(int)} provides a safe way to reference entities between
 * them, by clearing entity id of removed entity.
 * 
 * @author Joannick Gardize
 *
 */
@SkipInject
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
	private Bag<EntityReference> references;
	private IntBag recycleBin;
	private IntBag pendingRemoval;
	private Bag<InsertionEntry> pendingInsertion;
	private Bag<TransmutationEntry> pendingTransmutation;
	private int nextId;

	public EntityManager(int expectedEntityCount) {
		entities = new Bag<>(expectedEntityCount);
		references = new Bag<>(expectedEntityCount);
		int tmpCollectionsSize = expectedEntityCount / 10;
		recycleBin = new IntBag(tmpCollectionsSize);
		pendingInsertion = new Bag<>(tmpCollectionsSize);
		pendingRemoval = new IntBag(tmpCollectionsSize);
		pendingTransmutation = new Bag<>(tmpCollectionsSize);
		nextId = 0;
	}

	/**
	 * Creates a new entity for the given archetype. All components with a
	 * {@link ComponentCreationPolicy#AUTOMATIC} policy are immediately created, but
	 * the entity insertion will be effective for the next process cycle.
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
	 * Removes the given entity for the next process cycle.
	 * 
	 * @param entityId
	 */
	public void remove(int entityId) {
		pendingRemoval.add(entityId);
	}

	/**
	 * Transmutes the given entity into the given archetype. The new components with
	 * a {@link ComponentCreationPolicy#AUTOMATIC} policy are immediately created,
	 * but the transmutation will be effective for the next process cycle.
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

	/**
	 * <p>
	 * Provides a safe reference to the given entity. Once the entity is removed,
	 * the entity reference is cleared.
	 * <p>
	 * This is safe to get a reference of a newly created entity. This is also safe
	 * to use references during a {@link SubscriptionListener} event call.
	 * <p>
	 * The behavior of referencing a non-existing entity is undefined.
	 * 
	 * @param entityId the existing entity to reference
	 * @return the EntityReference instance referencing the given entity
	 */
	public EntityReference reference(int entityId) {
		EntityReference reference = references.getOrNull(entityId);
		if (reference == null) {
			reference = new EntityReference(entityId);
			references.put(entityId, reference);
			return reference;
		} else {
			return reference;
		}
	}

	/**
	 * Returns the actual archetype of the given entity.
	 * 
	 * @param entityId the entity id
	 * @return the actual archetype of the given entity
	 */
	public Archetype archetypeOf(int entityId) {
		return entities.get(entityId);
	}

	@Override
	public void process() {
		clearReferences();
		insertPending();
		transmutePending();
		removePending();
		nextId = entities.size();
	}

	private void clearReferences() {
		for (int i = 0, size = pendingRemoval.size(); i < size; i++) {
			int entityId = pendingRemoval.get(i);
			EntityReference reference = references.getOrNull(entityId);
			if (reference != null) {
				reference.clear();
				references.unsafeSet(entityId, null);
			}
		}
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

	private void transmutePending() {
		for (int i = 0, size = pendingTransmutation.size(); i < size; i++) {
			TransmutationEntry entry = pendingTransmutation.get(i);
			int id = entry.id;
			Transmutation transmutation = entry.transmutation;
			entities.unsafeSet(id, transmutation.getTo());
			for (Subscription subscription : transmutation.getAddSubscriptions()) {
				subscription.add(id);
			}
			for (Subscription subscription : transmutation.getChangeSubscriptions()) {
				subscription.notifyChanged(id, transmutation);
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

	private void removePending() {
		for (int i = 0, size = pendingRemoval.size(); i < size; i++) {
			int entityId = pendingRemoval.get(i);
			Archetype archetype = entities.get(entityId);
			if (archetype != null) {
				recycleBin.add(entityId);
				for (Subscription subscription : archetype.getSubscriptions()) {
					subscription.remove(entityId);
				}
				for (ComponentMapper<Object> mapper : archetype.getComponentMappers()) {
					mapper.remove(entityId);
				}
				entities.unsafeSet(entityId, null);
			}
		}
		pendingRemoval.clear();
	}
}
