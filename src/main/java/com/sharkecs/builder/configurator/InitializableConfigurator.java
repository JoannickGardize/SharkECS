package com.sharkecs.builder.configurator;

import com.sharkecs.Initializable;
import com.sharkecs.builder.EngineBuilder;

/**
 * {@link Configurator} of {@link Initializable}. Calling
 * {@link Initializable#initialize()}, usually after injection.
 * 
 * @author Joannick Gardize
 *
 */
public class InitializableConfigurator extends PrioritizedTypeConfigurator<Initializable> {

	public InitializableConfigurator() {
		super(Initializable.class);
	}

	@Override
	protected void prioritizedConfigure(Initializable object, EngineBuilder engineBuilder) {
		object.initialize();
	}

}
