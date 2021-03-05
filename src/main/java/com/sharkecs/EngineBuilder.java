package com.sharkecs;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EngineBuilder {

    private Map<Class<?>, ComponentMapper<Object>> componentMappers = new IdentityHashMap<>();
    private Map<String, Archetype> archetypes = new HashMap<>();
    private Set<Transmutation> transmutations = new HashSet<>();
    private Map<Class<? extends BaseSystem>, BaseSystem> systems = new LinkedHashMap<>();
    private int expectedEntityCount = 128;

    public EngineBuilder() {
        this(128);
    }

    public EngineBuilder(int expectedEntityCount) {
        if (expectedEntityCount < 1) {
            throw new IllegalArgumentException("expectedEntityCount must be greater than zero");
        }
        this.expectedEntityCount = expectedEntityCount;
    }

    public <T> void component(Class<T> type, Supplier<T> newInstanceSupplier) {
        componentMappers.put(type, new FlatArrayComponentMapper<>(expectedEntityCount, newInstanceSupplier));
    }

    public Archetype archetype(String name, Class<?>... componentTypes) {
        Archetype archetype = new Archetype(name, archetypes.size(), componentTypes);
        if (archetypes.put(name, archetype) != null) {
            throw new IllegalArgumentException("Archetype with name " + name + " already registered");
        }
        return archetype;
    }

    public void transmutation(Archetype from, Archetype to) {
        Transmutation transmutation = new Transmutation(from, to);
        if (!transmutations.add(transmutation)) {
            throw new IllegalArgumentException("Transmutation already registered: " + transmutation);
        }
    }

    public void transmutation(String from, String to) {
        Archetype fromArchetype = archetypes.get(from);
        if (fromArchetype == null) {
            throw new IllegalArgumentException("Unkown archetype: " + from);
        }
        Archetype toArchetype = archetypes.get(to);
        if (toArchetype == null) {
            throw new IllegalArgumentException("Unkown archetype: " + to);
        }
        transmutation(fromArchetype, toArchetype);
    }

    public void system(BaseSystem system) {
        if (systems.put(system.getClass(), system) != null) {
            throw new IllegalArgumentException(
                    "System of type " + system.getClass().getSimpleName() + " already registered");
        }
    }

    public Engine build() {
        Map<Aspect, Subscription> subscriptions = configureSystemAspects();
        configureArchetypes(subscriptions);
        configureTransmutations();
        // TODO
        return null;
    }

    private Map<Aspect, Subscription> configureSystemAspects() {
        Map<Aspect, Subscription> subscriptions = new HashMap<>();
        for (BaseSystem system : systems.values()) {
            system.setSubscription(subscriptions.computeIfAbsent(new Aspect(system.getClass()),
                    a -> new Subscription(expectedEntityCount)));
        }
        return subscriptions;
    }

    @SuppressWarnings("unchecked")
    private void configureArchetypes(Map<Aspect, Subscription> subscriptions) {
        for (Archetype archetype : archetypes.values()) {
            archetype.setSubscriptions(
                    subscriptions.entrySet().stream().filter(e -> e.getKey().matches(archetype.getComponentTypes()))
                            .map(Entry::getValue).toArray(Subscription[]::new));
            archetype.setComponentMappers(
                    archetype.getComponentTypes().stream().map(componentMappers::get).toArray(ComponentMapper[]::new));
            archetype.setTransmutations(new Transmutation[archetypes.size()]);
        }
    }

    private static class ArchetypeSets {
        Set<Subscription> subscriptions;
        @SuppressWarnings("rawtypes")
        Set<ComponentMapper> componentMappers;

        public ArchetypeSets(Archetype archetype) {
            subscriptions = new HashSet<>(Arrays.asList(archetype.getSubscriptions()));
            componentMappers = new HashSet<>(Arrays.asList(archetype.getComponentMappers()));
        }
    }

    @SuppressWarnings("unchecked")
    private void configureTransmutations() {
        Map<Archetype, ArchetypeSets> archetypeSets = new HashMap<>();

        for (Transmutation transmutation : transmutations) {
            ArchetypeSets from = archetypeSets.computeIfAbsent(transmutation.getFrom(), ArchetypeSets::new);
            ArchetypeSets to = archetypeSets.computeIfAbsent(transmutation.getTo(), ArchetypeSets::new);

            transmutation.getFrom().getTransmutations()[transmutation.getTo().getId()] = transmutation;

            transmutation.setAddSubscriptions(notContains(Subscription.class, to.subscriptions, from.subscriptions));
            transmutation.setRemoveSubscriptions(notContains(Subscription.class, from.subscriptions, to.subscriptions));
            transmutation.setChangeSubscriptions(contains(Subscription.class, from.subscriptions, to.subscriptions));

            transmutation.setAddMappers(notContains(ComponentMapper.class, to.componentMappers, from.componentMappers));
            transmutation
                    .setRemoveMappers(notContains(ComponentMapper.class, from.componentMappers, to.componentMappers));
        }
    }

    private <T> T[] notContains(Class<T> elementType, Set<T> compared, Set<T> comparing) {
        return filterToArray(elementType, compared, e -> !comparing.contains(e));
    }

    private <T> T[] contains(Class<T> elementType, Set<T> compared, Set<T> comparing) {
        return filterToArray(elementType, compared, comparing::contains);
    }

    @SuppressWarnings("unchecked")
    private <T> T[] filterToArray(Class<T> elementType, Set<T> initial, Predicate<T> matcher) {
        return initial.stream().filter(matcher).toArray(size -> (T[]) Array.newInstance(elementType, size));
    }
}