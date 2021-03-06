package com.sharkecs;

/**
 * A processor of something. the {@link #process()} method is intended to be
 * called successively, for instance, every frame of a game or a simulation.
 * 
 * @author Joannick Gardize
 *
 */
public interface Processor {

	/**
	 * Process this processor one time.
	 */
	void process();
}
