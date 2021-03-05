package com.sharkecs;

import java.util.Objects;

public class Transmutation {

    private Archetype from;
    private Archetype to;

    private Subscription[] addSubscriptions;
    private Subscription[] changeSubscriptions;
    private Subscription[] removeSubscriptions;

    private ComponentMapper<Object>[] addMappers;
    private ComponentMapper<Object>[] removeMappers;

    public Transmutation(Archetype from, Archetype to) {
        this.from = from;
        this.to = to;
    }

    public Archetype getFrom() {
        return from;
    }

    public Archetype getTo() {
        return to;
    }

    Subscription[] getAddSubscriptions() {
        return addSubscriptions;
    }

    void setAddSubscriptions(Subscription[] addSubscriptions) {
        this.addSubscriptions = addSubscriptions;
    }

    Subscription[] getChangeSubscriptions() {
        return changeSubscriptions;
    }

    void setChangeSubscriptions(Subscription[] changeSubscriptions) {
        this.changeSubscriptions = changeSubscriptions;
    }

    Subscription[] getRemoveSubscriptions() {
        return removeSubscriptions;
    }

    void setRemoveSubscriptions(Subscription[] removeSubscriptions) {
        this.removeSubscriptions = removeSubscriptions;
    }

    ComponentMapper<Object>[] getAddMappers() {
        return addMappers;
    }

    void setAddMappers(ComponentMapper<Object>[] addMappers) {
        this.addMappers = addMappers;
    }

    ComponentMapper<Object>[] getRemoveMappers() {
        return removeMappers;
    }

    void setRemoveMappers(ComponentMapper<Object>[] removeMappers) {
        this.removeMappers = removeMappers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Transmutation)) {
            return false;
        } else {
            Transmutation other = (Transmutation) obj;
            return Objects.equals(from, other.from) && Objects.equals(to, other.to);
        }
    }

    @Override
    public String toString() {
        return "Transmutation (" + from.getName() + " -> " + to.getName() + ")";
    }
}
