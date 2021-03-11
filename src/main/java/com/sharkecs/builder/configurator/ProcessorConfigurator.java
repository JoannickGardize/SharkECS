package com.sharkecs.builder.configurator;

import com.sharkecs.Engine;
import com.sharkecs.Processor;
import com.sharkecs.builder.EngineBuilder;

/**
 * {@link Configurator} of {@link Processor}. They are added to the
 * {@link Engine}, in their registration order.
 * 
 * @author Joannick Gardize
 *
 */
public class ProcessorConfigurator extends PrioritizedTypeConfigurator<Processor> {

	public ProcessorConfigurator() {
		super(Processor.class);
	}

	@Override
	protected void prioritizedConfigure(Processor object, EngineBuilder engineBuilder) {
		engineBuilder.addProcessor(object);

	}
}
