package com.sharkecs;

import com.sharkecs.annotation.SkipInject;

/**
 * <p>
 * Represents a transmutation operation, changing an entity of a given archetype
 * to another one.
 * 
 * <p>
 * Stores the {@link Subscription}s and {@link ComponentMapper}s associated with
 * this transmutation, these arrays must not be manually modified, or unexpected
 * behaviors may occurs.
 * 
 * @author Joannick Gardize
 *
 */
@SkipInject
public class Transmutation {

	// Construction attributes
	private Archetype from;
	private Archetype to;

	// Computed attributes
	private Subscription[] addSubscriptions;
	private Subscription[] changeSubscriptions;
	private Subscription[] removeSubscriptions;
	private ComponentMapper<Object>[] addMappers;
	private ComponentMapper<Object>[] removeMappers;
	private boolean configured;

	public Transmutation(Archetype from, Archetype to) {
		this.from = from;
		this.to = to;
		configured = false;
	}

	public Archetype getFrom() {
		return from;
	}

	public Archetype getTo() {
		return to;
	}

	public Subscription[] getAddSubscriptions() {
		return addSubscriptions;
	}

	public void setAddSubscriptions(Subscription[] addSubscriptions) {
		checkConfigured();
		this.addSubscriptions = addSubscriptions;
	}

	public Subscription[] getChangeSubscriptions() {
		return changeSubscriptions;
	}

	public void setChangeSubscriptions(Subscription[] changeSubscriptions) {
		checkConfigured();
		this.changeSubscriptions = changeSubscriptions;
	}

	public Subscription[] getRemoveSubscriptions() {
		return removeSubscriptions;
	}

	public void setRemoveSubscriptions(Subscription[] removeSubscriptions) {
		checkConfigured();
		this.removeSubscriptions = removeSubscriptions;
	}

	public ComponentMapper<Object>[] getAddMappers() {
		return addMappers;
	}

	public void setAddMappers(ComponentMapper<Object>[] addMappers) {
		checkConfigured();
		this.addMappers = addMappers;
	}

	public ComponentMapper<Object>[] getRemoveMappers() {
		return removeMappers;
	}

	public void setRemoveMappers(ComponentMapper<Object>[] removeMappers) {
		checkConfigured();
		this.removeMappers = removeMappers;
	}

	@Override
	public String toString() {
		return "Transmutation (" + from.getName() + " -> " + to.getName() + ")";
	}

	public void markConfigured() {
		configured = true;
	}

	private void checkConfigured() {
		if (configured) {
			throw new IllegalStateException("the transmutation is already configured");
		}
	}
}
