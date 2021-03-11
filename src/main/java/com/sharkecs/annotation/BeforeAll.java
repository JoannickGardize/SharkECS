package com.sharkecs.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sharkecs.builder.configurator.Configurator;
import com.sharkecs.builder.configurator.RootConfigurator;

/**
 * Annotation for {@link Configurator} types to indicates to the
 * {@link RootConfigurator} that the configurator should be executed first
 * whatever.
 * 
 * @author Joannick Gardize
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface BeforeAll {

}
