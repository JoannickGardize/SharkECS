package com.sharkecs.builder;

/**
 * A configurator of object. Called by {@link EngineBuilder} during
 * {@link EngineBuilder#build()} for each assignable registered objects.
 * 
 * @author Joannick Gardize
 *
 * @param <T>
 */
public interface Configurator<T> {

	void configure(T object, EngineBuilder engineBuilder);
}
