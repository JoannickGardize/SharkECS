package com.sharkecs;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.sharkecs.annotation.CreationPolicy;
import com.sharkecs.annotation.SkipInjection;
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
@SkipInjection
public class Archetype {

	/**
	 * The policy to apply when an entity gain a new component
	 * 
	 * @author Joannick Gardize
	 *
	 */
	public enum ComponentCreationPolicy {
		/**
		 * The component mapper will automatically create the component instance when
		 * the entity gain it.
		 */
		AUTOMATIC,
		/**
		 * <p>
		 * The component mapper won't do anything when the entity gain the component.
		 * <p>
		 * The user is usually supposed to manually call
		 * {@link ComponentMapper#create(int)} or
		 * {@link ComponentMapper#put(int, Object)} when the entity gain the component
		 * via creation or mutation.
		 */
		MANUAL;
	}

	private String name;
	private int id;
	private Set<Class<?>> componentTypesSet;
	private Map<Class<?>, ComponentCreationPolicy> componentTypes;

	private Subscription[] subscriptions;
	private ComponentMapper<Object>[] componentMappers;
	private ComponentMapper<Object>[] autoCreateComponentMappers;
	private Transmutation[] transmutations;

	public Archetype(String name, int id, Class<?>... componentTypes) {
		this.name = name;
		this.id = id;
		this.componentTypes = new IdentityHashMap<>();
		for (Class<?> componentType : componentTypes) {
			this.componentTypes.put(componentType, null);
		}
		componentTypesSet = Collections.unmodifiableSet(this.componentTypes.keySet());
	}

	/**
	 * <p>
	 * Configure the given component type policy at entity creation or mutation for
	 * the given component types, this override the default and component's type
	 * annotation setting.
	 * <p>
	 * Calling this method after the engine building has no effect.
	 * 
	 * @param autoCreation   true for the component to be automatically created at
	 *                       entity creation and mutation, false otherwise.
	 * @param componentTypes the component types to set the auto creation setting
	 */
	public void setComponentCreationPolicy(ComponentCreationPolicy componentCreationPolicy, Class<?>... componentTypes) {
		for (Class<?> componentType : componentTypes) {
			if (!this.componentTypes.containsKey(componentType)) {
				throw new EngineConfigurationException("the component type " + componentType.getClass() + " is not present for the archetype " + this);
			}
			this.componentTypes.put(componentType, componentCreationPolicy);
		}
	}

	/**
	 * Set the auto creation parameter for all component types of this archetype, as
	 * defined in {@link #setComponentCreationPolicy(boolean, Class...)}.
	 * 
	 * @param autoCreation
	 */
	public void setComponentCreationPolicy(ComponentCreationPolicy componentCreationPolicy) {
		for (Entry<Class<?>, ComponentCreationPolicy> entry : componentTypes.entrySet()) {
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
		ComponentCreationPolicy archetypeValue = componentTypes.get(componentType);
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

	public Set<Class<?>> getComponentTypes() {
		return componentTypesSet;
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
		return "Archetype " + name + componentTypesSet.stream().map(t -> t.getClass().getSimpleName()).collect(Collectors.joining(", ", " (", ")"));
	}
}
