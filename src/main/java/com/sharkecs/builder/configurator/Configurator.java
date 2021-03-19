package com.sharkecs.builder.configurator;

import com.sharkecs.builder.EngineBuilder;

/**
 * A configurator of something, called during {@link EngineBuilder#build()}
 * (when using the default {@link RootConfigurator}).
 * 
 * @author Joannick Gardize
 *
 */
public interface Configurator {

	/**
	 * @param engineBuilder the EngineBuilder to configure
	 */
	void configure(EngineBuilder engineBuilder);
}
