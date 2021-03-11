package com.sharkecs.builder.configurator;

import com.sharkecs.Engine;
import com.sharkecs.builder.EngineBuilder;

/**
 * A configurator of {@link Engine}, called during {@link EngineBuilder#build()}
 * (when using the default {@link RootConfigurator}).
 * 
 * @author Joannick Gardize
 *
 */
public interface Configurator {

	void configure(EngineBuilder engineBuilder);
}
