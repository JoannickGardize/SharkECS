package com.sharkecs;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.sharkecs.annotation.CreationPolicy;
import com.sharkecs.annotation.SkipInject;
import com.sharkecs.builder.EngineConfigurationException;

/**
 * <p>
 * Represents the component composition of an entity.
 * <p>
 * Stores the {@link Subscription}s, {@link ComponentMapper}s and
 * {@link Transmutation}s associated with this archetype, these arrays must not
 * be manually modified, or unexpected behaviors may occurs.
 * 
 * @author Joannick Gardize
 *
 */
@SkipInject
public class Archetype {

	/**
	 * The policy to apply to a component when an entity gain a new component
	 * 
	 * @author Joannick Gardize
	 *
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

	private String name;
	private int id;
	private Set<Class<?>> compositionSet;
	private Map<Class<?>, ComponentCreationPolicy> composition;

	private Subscription[] subscriptions;
	private ComponentMapper<Object>[] componentMappers;
	private ComponentMapper<Object>[] autoCreateComponentMappers;
	private Transmutation[] transmutations;

	public Archetype(String name, int id, Class<?>... componentTypes) {
		this.name = name;
		this.id = id;
		this.composition = new IdentityHashMap<>();
		for (Class<?> componentType : componentTypes) {
			this.composition.put(componentType, null);
		}
		compositionSet = Collections.unmodifiableSet(this.composition.keySet());
	}

	/**
	 * <p>
	 * Configure the {@link ComponentCreationPolicy} for the given component types.
	 * This configuration overrides the default setting and the component's type
	 * annotation setting.
	 * <p>
	 * Calling this method after the engine building has no effect.
	 * 
	 * @param componentCreationPolicy the {@link ComponentCreationPolicy} to apply
	 *                                to the given component types
	 * @param componentTypes          the component types to set the
	 *                                {@link ComponentCreationPolicy} setting
	 */
	public void setComponentCreationPolicy(ComponentCreationPolicy componentCreationPolicy, Class<?>... componentTypes) {
		for (Class<?> componentType : componentTypes) {
			if (!this.composition.containsKey(componentType)) {
				throw new EngineConfigurationException("the component type " + componentType.getClass() + " is not present for the archetype " + this);
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
	 */
	public void setComponentCreationPolicy(ComponentCreationPolicy componentCreationPolicy) {
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
	public ComponentCreationPolicy getComponentCreationPolicy(Class<?> componentType, ComponentCreationPolicy defaultValue) {
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

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	/**
	 * @return the component composition of the archetype
	 */
	public Set<Class<?>> getComposition() {
		return compositionSet;
	}

	public Subscription[] getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Subscription[] subscriptions) {
		this.subscriptions = subscriptions;
	}

	public ComponentMapper<Object>[] getComponentMappers() {
		return componentMappers;
	}

	public void setComponentMappers(ComponentMapper<Object>[] componentMappers) {
		this.componentMappers = componentMappers;
	}

	public ComponentMapper<Object>[] getAutoCreateComponentMappers() {
		return autoCreateComponentMappers;
	}

	public void setAutoCreateComponentMappers(ComponentMapper<Object>[] autoCreateComponentMappers) {
		this.autoCreateComponentMappers = autoCreateComponentMappers;
	}

	public void setTransmutations(Transmutation[] transmutations) {
		this.transmutations = transmutations;
	}

	public Transmutation[] getTransmutations() {
		return transmutations;
	}

	@Override
	public String toString() {
		return "Archetype " + name + compositionSet.stream().map(t -> t.getClass().getSimpleName()).collect(Collectors.joining(", ", " (", ")"));
	}
}
