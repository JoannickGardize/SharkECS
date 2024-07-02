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

import sharkhendrix.sharkecs.annotation.CreationPolicy;
import sharkhendrix.sharkecs.annotation.SkipInject;
import sharkhendrix.sharkecs.builder.EngineConfigurationException;
import sharkhendrix.sharkecs.subscription.Subscription;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Represents the component composition of an entity.
 * <p>
 * Stores the computed {@link Subscription}s, {@link ComponentMapper}s and
 * {@link Transmutation}s associated with this archetype.
 */
@SkipInject
public class Archetype {

    /**
     * The policy to apply to a component when an entity gain a new component
     */
    public enum ComponentCreationPolicy {
        /**
         * The component mapper will automatically create the component instance when
         * the entity gains it.
         */
        AUTOMATIC,
        /**
         * <p>
         * The component mapper won't do anything when the entity gains the component
         * type.
         * <p>
         * The user is intended to manually call {@link ComponentMapper#create(int)} or
         * {@link ComponentMapper#put(int, Object)} when the entity gain the component
         * via creation or mutation.
         */
        MANUAL;
    }

    // Construction attributes
    private String name;
    private Set<Class<?>> compositionSet;
    private Map<Class<?>, ComponentCreationPolicy> composition;

    // Computed attributes
    private int id;
    private Subscription[] subscriptions;
    private ComponentMapper<Object>[] componentMappers;
    private ComponentMapper<Object>[] autoCreateComponentMappers;
    private Transmutation[] transmutations;
    private Map<Class<?>, Transmutation> additiveTransmutations;
    private Map<Class<?>, Transmutation> suppressiveTransmutations;
    private boolean configured;

    public Archetype(String name, Class<?>... componentTypes) {
        this.name = name;
        this.composition = new IdentityHashMap<>();
        for (Class<?> componentType : componentTypes) {
            this.composition.put(componentType, null);
        }
        compositionSet = Collections.unmodifiableSet(this.composition.keySet());
        configured = false;
    }

    /**
     * Configure the {@link ComponentCreationPolicy} for the given component types.
     * This configuration overrides the default setting and the component's type
     * annotation setting.
     *
     * @param componentCreationPolicy the {@link ComponentCreationPolicy} to apply
     *                                to the given component types
     * @param componentTypes          the component types to set the
     *                                {@link ComponentCreationPolicy} setting
     * @throws IllegalStateException if this archetype configuration is already done
     */
    public void setComponentCreationPolicy(ComponentCreationPolicy componentCreationPolicy,
                                           Class<?>... componentTypes) {
        checkConfigured();
        for (Class<?> componentType : componentTypes) {
            if (!this.composition.containsKey(componentType)) {
                throw new EngineConfigurationException(
                        "the component type " + componentType.getClass() + " is not present for the archetype " + this);
            }
            this.composition.put(componentType, componentCreationPolicy);
        }
    }

    /**
     * Set the {@link ComponentCreationPolicy} for all component types of this
     * archetype.
     *
     * @param componentCreationPolicy the {@link ComponentCreationPolicy} to apply
     *                                to all component types of this archetype
     * @throws IllegalStateException if this archetype configuration is already done
     */
    public void setComponentCreationPolicy(ComponentCreationPolicy componentCreationPolicy) {
        checkConfigured();
        for (Entry<Class<?>, ComponentCreationPolicy> entry : composition.entrySet()) {
            entry.setValue(componentCreationPolicy);
        }
    }

    /**
     * <p>
     * Get the component creation policy of the given component type.
     * <p>
     * If this archetype has manually defined a policy via
     * {@link #setComponentCreationPolicy(ComponentCreationPolicy, Class...)} or
     * {@link #setComponentCreationPolicy(ComponentCreationPolicy)}, this policy is
     * returned. If not, if the component class has defined a {@link CreationPolicy}
     * annotation, its value is returned. If not, the provided default value is
     * returned.
     *
     * @param componentType
     * @param defaultValue
     * @return
     */
    public ComponentCreationPolicy getComponentCreationPolicy(Class<?> componentType,
                                                              ComponentCreationPolicy defaultValue) {
        ComponentCreationPolicy archetypeValue = composition.get(componentType);
        if (archetypeValue != null) {
            return archetypeValue;
        } else {
            CreationPolicy autoCreation = componentType.getAnnotation(CreationPolicy.class);
            if (autoCreation != null) {
                return autoCreation.value();
            } else {
                return defaultValue;
            }
        }
    }

    /**
     * @return the name of the archetype
     */
    public String getName() {
        return name;
    }

    /**
     * @return the component composition of the archetype
     */
    public Set<Class<?>> getComposition() {
        return compositionSet;
    }

    public int getId() {
        return id;
    }

    /**
     * @param id
     * @throws IllegalStateException if this archetype configuration is already done
     */
    public void setId(int id) {
        checkConfigured();
        this.id = id;
    }

    public Subscription[] getSubscriptions() {
        return subscriptions;
    }

    /**
     * @param subscriptions
     * @throws IllegalStateException if this archetype configuration is already done
     */
    public void setSubscriptions(Subscription[] subscriptions) {
        checkConfigured();
        this.subscriptions = subscriptions;
    }

    public ComponentMapper<Object>[] getComponentMappers() {
        return componentMappers;
    }

    /**
     * @param componentMappers
     * @throws IllegalStateException if this archetype configuration is already done
     */
    public void setComponentMappers(ComponentMapper<Object>[] componentMappers) {
        checkConfigured();
        this.componentMappers = componentMappers;
    }

    public ComponentMapper<Object>[] getAutoCreateComponentMappers() {
        return autoCreateComponentMappers;
    }

    /**
     * @param autoCreateComponentMappers
     * @throws IllegalStateException if this archetype configuration is already done
     */
    public void setAutoCreateComponentMappers(ComponentMapper<Object>[] autoCreateComponentMappers) {
        checkConfigured();
        this.autoCreateComponentMappers = autoCreateComponentMappers;
    }

    public Transmutation[] getTransmutations() {
        return transmutations;
    }

    /**
     * @param transmutations
     * @throws IllegalStateException if this archetype configuration is already done
     */
    public void setTransmutations(Transmutation[] transmutations) {
        checkConfigured();
        this.transmutations = transmutations;
    }

    /**
     * @return a map containing all transmutations that exclusively add the
     * component type in the key
     */
    public Map<Class<?>, Transmutation> getAdditiveTransmutations() {
        return additiveTransmutations;
    }

    public void setAdditiveTransmutations(Map<Class<?>, Transmutation> additiveTransmutations) {
        checkConfigured();
        this.additiveTransmutations = additiveTransmutations;
    }

    /**
     * @return a map containing all transmutations that exclusively remove the
     * component type in the key
     */
    public Map<Class<?>, Transmutation> getSuppressiveTransmutations() {
        return suppressiveTransmutations;
    }

    public void setSuppressiveTransmutations(Map<Class<?>, Transmutation> suppressiveTransmutations) {
        checkConfigured();
        this.suppressiveTransmutations = suppressiveTransmutations;
    }

    @Override
    public String toString() {
        return "Archetype " + name + compositionSet.stream().map(t -> t.getClass().getSimpleName())
                .collect(Collectors.joining(", ", " (", ")"));
    }

    public void markConfigured() {
        configured = true;
    }

    private void checkConfigured() {
        if (configured) {
            throw new IllegalStateException("the archetype is already configured");
        }
    }
}
