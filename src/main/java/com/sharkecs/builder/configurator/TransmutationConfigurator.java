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

package com.sharkecs.builder.configurator;

import com.sharkecs.Archetype;
import com.sharkecs.ComponentMapper;
import com.sharkecs.Subscription;
import com.sharkecs.Transmutation;
import com.sharkecs.builder.EngineBuilder;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;

/**
 * {@link Configurator} of {@link Transmutation}s. Computes the difference of
 * {@link Subscription} and {@link ComponentMapper} between the "from" and the
 * "to" {@link Archetype}, for a fast transmutation operation at runtime.
 *
 * @author Joannick Gardize
 */
public class TransmutationConfigurator extends TypeConfigurator<Transmutation> {

    private static class ArchetypeSets {
        Set<Subscription> subscriptions;
        @SuppressWarnings("rawtypes")
        Set<ComponentMapper> autoCreateComponentMappers;
        @SuppressWarnings("rawtypes")
        Set<ComponentMapper> componentMappers;

        public ArchetypeSets(Archetype archetype) {
            subscriptions = new HashSet<>(Arrays.asList(archetype.getSubscriptions()));
            autoCreateComponentMappers = new HashSet<>(Arrays.asList(archetype.getAutoCreateComponentMappers()));
            componentMappers = new HashSet<>(Arrays.asList(archetype.getComponentMappers()));
        }
    }

    private Map<Archetype, ArchetypeSets> archetypeSets = new HashMap<>();

    public TransmutationConfigurator() {
        super(Transmutation.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configure(Transmutation transmutation, EngineBuilder engineBuilder) {
        ArchetypeSets from = archetypeSets.computeIfAbsent(transmutation.getFrom(), ArchetypeSets::new);
        ArchetypeSets to = archetypeSets.computeIfAbsent(transmutation.getTo(), ArchetypeSets::new);

        transmutation.getFrom().getTransmutations()[transmutation.getTo().getId()] = transmutation;

        transmutation.setAddSubscriptions(notContains(Subscription.class, to.subscriptions, from.subscriptions));
        transmutation.setRemoveSubscriptions(notContains(Subscription.class, from.subscriptions, to.subscriptions));
        transmutation.setChangeSubscriptions(contains(Subscription.class, from.subscriptions, to.subscriptions));

        transmutation.setAddMappers(
                notContains(ComponentMapper.class, to.autoCreateComponentMappers, from.componentMappers));
        transmutation.setRemoveMappers(notContains(ComponentMapper.class, from.componentMappers, to.componentMappers));

        addVariantAccessor(transmutation);

        transmutation.markConfigured();
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

    private void addVariantAccessor(Transmutation transmutation) {
        Set<Class<?>> fromComposition = transmutation.getFrom().getComposition();
        Set<Class<?>> toComposition = transmutation.getTo().getComposition();
        if (fromComposition.size() + 1 == toComposition.size()) {
            Class<?> extra = equalsAndGetExtra(fromComposition, toComposition);
            if (extra != null) {
                transmutation.getFrom().getAdditiveTransmutations().put(extra, transmutation);
            }
        } else if (fromComposition.size() - 1 == toComposition.size()) {
            Class<?> extra = equalsAndGetExtra(toComposition, fromComposition);
            if (extra != null) {
                transmutation.getFrom().getSuppressiveTransmutations().put(extra, transmutation);
            }
        }
    }

    /**
     * {@code setWithExtra} should extractly have a size of {@code set.size() + 1}
     *
     * @param <T>
     * @param set
     * @param setWithExtra
     * @return the extra element of setWithExtra compared to set, or null if any
     * other element is different
     */
    private <T> T equalsAndGetExtra(Set<T> set, Set<T> setWithExtra) {
        T extra = null;
        for (T e : setWithExtra) {
            if (!set.contains(e)) {
                if (extra == null) {
                    extra = e;
                } else {
                    return null;
                }
            }
        }
        return extra;
    }
}
