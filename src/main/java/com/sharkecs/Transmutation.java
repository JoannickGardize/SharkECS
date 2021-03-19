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

	public Subscription[] getAddSubscriptions() {
		return addSubscriptions;
	}

	public void setAddSubscriptions(Subscription[] addSubscriptions) {
		this.addSubscriptions = addSubscriptions;
	}

	public Subscription[] getChangeSubscriptions() {
		return changeSubscriptions;
	}

	public void setChangeSubscriptions(Subscription[] changeSubscriptions) {
		this.changeSubscriptions = changeSubscriptions;
	}

	public Subscription[] getRemoveSubscriptions() {
		return removeSubscriptions;
	}

	public void setRemoveSubscriptions(Subscription[] removeSubscriptions) {
		this.removeSubscriptions = removeSubscriptions;
	}

	public ComponentMapper<Object>[] getAddMappers() {
		return addMappers;
	}

	public void setAddMappers(ComponentMapper<Object>[] addMappers) {
		this.addMappers = addMappers;
	}

	public ComponentMapper<Object>[] getRemoveMappers() {
		return removeMappers;
	}

	public void setRemoveMappers(ComponentMapper<Object>[] removeMappers) {
		this.removeMappers = removeMappers;
	}

	@Override
	public String toString() {
		return "Transmutation (" + from.getName() + " -> " + to.getName() + ")";
	}
}
