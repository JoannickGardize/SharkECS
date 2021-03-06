package com.sharkecs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.sharkecs.injection.SkipInjection;

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

	private String name;
	private int id;
	private Set<Class<?>> componentTypes;

	private Subscription[] subscriptions;
	private ComponentMapper<Object>[] componentMappers;
	private Transmutation[] transmutations;

	public Archetype(String name, int id, Class<?>... componentTypes) {
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
		return "Archetype " + name + componentTypes.stream().map(t -> t.getClass().getSimpleName())
		        .collect(Collectors.joining(", ", " (", ")"));
	}
}
