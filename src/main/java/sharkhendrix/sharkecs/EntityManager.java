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

package sharkhendrix.sharkecs;

import sharkhendrix.sharkecs.Archetype.ComponentCreationPolicy;
import sharkhendrix.sharkecs.annotation.SkipInject;
import sharkhendrix.sharkecs.subscription.Subscription;
import sharkhendrix.sharkecs.subscription.SubscriptionListener;
import sharkhendrix.sharkecs.util.Bag;
import sharkhendrix.sharkecs.util.IntBag;

/**
 * <p>
 * Manage all entities. Provides entity creation, deletion, and mutation
 * operations. These three operations are delayed and effective for the next
 * process cycle.
 * <p>
 * {@link #reference(int)} provides a safe way to reference entities between
 * them, by clearing entity id of removed entity.
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
     * @param entity
     */
    public void remove(int entity) {
        pendingRemoval.add(entity);
    }

    /**
     * Transmutes the given entity into the given archetype. This call will
     * immediately:
     * <ul>
     * <li>Create components with a {@link ComponentCreationPolicy#AUTOMATIC}
     * policy.
     * <li>Change the archetype returned by {@link #archetypeOf(int)} with the new
     * one.
     * </ul>
     * Other changes will be effective for the next process cycle:
     * <ul>
     * <li>Remove lost components.
     * <li>Add, remove, and notify {@link Subscription}s accordingly.
     * </ul>
     *
     * @param entity
     * @param toArchetype
     */
    public void transmute(int entity, Archetype toArchetype) {
        Archetype archetype = entities.get(entity);
        transmute(entity, archetype.getTransmutations()[toArchetype.getId()]);
    }

    /**
     * Transmutes the given entity the same way as
     * {@link #transmute(int, Archetype)}, but look for the transmutation that
     * exactly adds the given component type.
     *
     * @param entity
     * @param componentType
     * @throws NullPointerException if such a transmutation does not exists
     */
    public void addComponent(int entity, Class<?> componentType) {
        Archetype archetype = entities.get(entity);
        transmute(entity, archetype.getAdditiveTransmutations().get(componentType));
    }

    /**
     * Transmutes the given entity the same way as
     * {@link #transmute(int, Archetype)}, but look for the transmutation that
     * exactly removes the given component type.
     *
     * @param entity
     * @param componentType
     * @throws NullPointerException if such a transmutation does not exists
     */
    public void removeComponent(int entity, Class<?> componentType) {
        Archetype archetype = entities.get(entity);
        transmute(entity, archetype.getSuppressiveTransmutations().get(componentType));
    }

    private void transmute(int entity, Transmutation transmutation) {
        entities.unsafeSet(entity, transmutation.getTo());
        for (ComponentMapper<Object> mapper : transmutation.getAddMappers()) {
            mapper.create(entity);
        }
        TransmutationEntry entry = pendingTransmutation.nextOrAdd(TransmutationEntry::new);
        entry.id = entity;
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
     * @param entity the existing entity to reference
     * @return the EntityReference instance referencing the given entity
     */
    public EntityReference reference(int entity) {
        EntityReference reference = references.getOrNull(entity);
        if (reference == null) {
            reference = new EntityReference(entity);
            references.put(entity, reference);
        }
        return reference;
    }

    /**
     * Returns the actual (or future, if a mutation has occurred during this process
     * cycle) archetype of the given entity.
     *
     * @param entity the entity id
     * @return the actual or future archetype of the given entity
     */
    public Archetype archetypeOf(int entity) {
        return entities.get(entity);
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
            int entity = pendingRemoval.get(i);
            EntityReference reference = references.getOrNull(entity);
            if (reference != null) {
                reference.clear();
                references.unsafeSet(entity, null);
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
            int entity = pendingRemoval.get(i);
            Archetype archetype = entities.get(entity);
            if (archetype != null) {
                recycleBin.add(entity);
                for (Subscription subscription : archetype.getSubscriptions()) {
                    subscription.remove(entity);
                }
                for (ComponentMapper<Object> mapper : archetype.getComponentMappers()) {
                    mapper.remove(entity);
                }
                entities.unsafeSet(entity, null);
            }
        }
        pendingRemoval.clear();
    }
}
