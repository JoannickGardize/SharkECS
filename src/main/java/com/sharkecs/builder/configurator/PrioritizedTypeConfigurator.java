package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.List;

import com.sharkecs.builder.EngineBuilder;

/**
 * A {@link TypeConfigurator} using the {@link Prioritizer} to iterate
 * over elements according to their priority.
 * 
 * @author Joannick Gardize
 *
 * @param <T>
 */
public abstract class PrioritizedTypeConfigurator<T> extends TypeConfigurator<T> {

	private List<T> elements = new ArrayList<>();

	protected PrioritizedTypeConfigurator(Class<T> type) {
		super(type);
	}

	@Override
	protected final void configure(T object, EngineBuilder engineBuilder) {
		elements.add(object);
	}

	@Override
	protected void endConfiguration(EngineBuilder engineBuilder) {
		engineBuilder.getRegistrations().get(Prioritizer.class).prioritize(elements);
		elements.forEach(o -> prioritizedConfigure(o, engineBuilder));
	}

	protected abstract void prioritizedConfigure(T object, EngineBuilder engineBuilder);
}
