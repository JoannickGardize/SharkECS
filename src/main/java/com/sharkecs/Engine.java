package com.sharkecs;

import com.sharkecs.builder.EngineBuilder;

/**
 * Root class of an ECS run. It is simply made of an array of {@link Processor}.
 * It is intended to be created via an {@link EngineBuilder}, which will
 * configure and link all elements together.
 * 
 * @author Joannick Gardize
 *
 */
public class Engine {

	private Processor[] processors;

	public Engine(Processor[] processors) {
		this.processors = processors;
	}

	/**
	 * Process successively all {@link Processor}s of this engine. This is intended
	 * to be called successively, for instance, every frame of a game or a
	 * simulation.
	 */
	public void process() {
		for (int i = 0, size = processors.length; i < size; i++) {
			processors[i].process();
		}
	}

	public Processor[] getProcessors() {
		return processors;
	}
}
