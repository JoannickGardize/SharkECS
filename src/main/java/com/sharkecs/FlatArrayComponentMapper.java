package com.sharkecs;

import java.util.function.Supplier;

import com.sharkecs.util.Bag;

/**
 * Simplest and fastest implementation of {@link ComponentMapper}, but has the
 * worst memory print, especially for components that are rare among all
 * entities.
 * 
 * @author Joannick Gardize
 *
 * @param <T> the component type
 */
public class FlatArrayComponentMapper<T> implements ComponentMapper<T> {

	private Bag<T> components;
	private Supplier<? extends T> newInstanceSupplier;

	public FlatArrayComponentMapper(int initialCapacity, Supplier<? extends T> newInstanceSupplier) {
		components = new Bag<>(initialCapacity);
		this.newInstanceSupplier = newInstanceSupplier;
	}

	@Override
	public T create(int entityId) {
		T component = newInstanceSupplier.get();
		components.unsafeSet(entityId, component);
		return component;
	}

	@Override
	public void put(int entityId, T component) {
		components.unsafeSet(entityId, component);
	}

	@Override
	public void remove(int entityId) {
		components.unsafeSet(entityId, null);
	}

	@Override
	public T get(int entityId) {
		return components.get(entityId);
	}

}
