package com.sharkecs.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sharkecs.builder.configurator.Injector;

/**
 * Indicates to the {@link Injector} to exclude the annotated field for
 * injection, or all fields if annotated to a class.
 * 
 * @author Joannick Gardize
 *
 */
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface SkipInjection {

}
