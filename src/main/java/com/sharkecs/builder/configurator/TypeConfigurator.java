package com.sharkecs.builder.configurator;

import com.sharkecs.builder.EngineBuilder;

/**
 * A {@link Configurator} iterating other all registered objects assignable from
 * a given type.
 * 
 * @author Joannick Gardize
 *
 * @param <T>
 */
public abstract class TypeConfigurator<T> implements Configurator {

	private Class<T> type;

	protected TypeConfigurator(Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configure(EngineBuilder engineBuilder) {
		beginConfiguration(engineBuilder);
		engineBuilder.getRegistrations().forEachAssignableFrom(type, o -> configure((T) o, engineBuilder));
		endConfiguration(engineBuilder);
	}

	protected void beginConfiguration(EngineBuilder engineBuilder) {
		// Nothing by default
	}

	protected abstract void configure(T object, EngineBuilder engineBuilder);

	protected void endConfiguration(EngineBuilder engineBuilder) {
// Nothing by default
	}

}
