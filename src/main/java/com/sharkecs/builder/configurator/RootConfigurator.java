package com.sharkecs.builder.configurator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sharkecs.annotation.BeforeAll;
import com.sharkecs.builder.EngineBuilder;

/**
 * <p>
 * The default root configurator for the {@link EngineBuilder}.
 * <p>
 * First, it calls all registered {@link Configurator}s annotated with
 * {@link BeforeAll}, then, it calls all other registered {@link Configurator}s
 * using a {@link Prioritizer} to call them in the right order (So the
 * {@link Prioritizer} is obviously marked with {@link BeforeAll}).
 * 
 * @author Joannick Gardize
 *
 */
public class RootConfigurator extends TypeConfigurator<Configurator> {

	private List<Configurator> configurators = new ArrayList<>();

	public RootConfigurator() {
		super(Configurator.class);
	}

	@Override
	protected void configure(Configurator object, EngineBuilder engineBuilder) {
		configurators.add(object);
	}

	@Override
	protected void endConfiguration(EngineBuilder engineBuilder) {
		Iterator<Configurator> it = configurators.iterator();
		while (it.hasNext()) {
			Configurator configurator = it.next();
			if (configurator.getClass().isAnnotationPresent(BeforeAll.class)) {
				configurator.configure(engineBuilder);
				it.remove();
			}
		}
		engineBuilder.getRegistrations().getOrFail(Prioritizer.class).prioritize(configurators);
		configurators.forEach(c -> c.configure(engineBuilder));
	}
}
