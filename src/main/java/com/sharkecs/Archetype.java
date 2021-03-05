package com.sharkecs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Archetype {

    private String name;
    private int id;
    private Set<Class<?>> componentTypes;

    private Subscription[] subscriptions;
    private ComponentMapper<Object>[] componentMappers;
    private Transmutation[] transmutations;

    Archetype(String name, int id, Class<?>... componentTypes) {
        this.name = name;
        this.id = id;
        this.componentTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(componentTypes)));
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Set<Class<?>> getComponentTypes() {
        return componentTypes;
    }

    Subscription[] getSubscriptions() {
        return subscriptions;
    }

    void setSubscriptions(Subscription[] subscriptions) {
        this.subscriptions = subscriptions;
    }

    ComponentMapper<Object>[] getComponentMappers() {
        return componentMappers;
    }

    void setComponentMappers(ComponentMapper<Object>[] componentMappers) {
        this.componentMappers = componentMappers;
    }

    void setTransmutations(Transmutation[] transmutations) {
        this.transmutations = transmutations;
    }

    Transmutation[] getTransmutations() {
        return transmutations;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Archetype)) {
            return false;
        } else {
            Archetype other = (Archetype) obj;
            return id == other.id;
        }
    }

    @Override
    public String toString() {
        return "Archetype " + name + componentTypes.stream().map(t -> t.getClass().getSimpleName())
                .collect(Collectors.joining(", ", " (", ")"));
    }
}
