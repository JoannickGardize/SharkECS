package com.sharkecs;

/**
 * Holder of an initialization method that must be called at the very end of an
 * Engine build.
 * 
 * @author Joannick Gardize
 *
 */
public interface Initializable {

	void initialize();
}
