package com.sharkecs.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sharkecs.builder.configurator.Injector;

/**
 * Annotation for field or class to indicates to the {@link Injector} that the
 * annotated field or class must be injected, even if this is a superclass and
 * {@link Inject#injectParent()} is missing or not set to true on all
 * subclasses.
 * 
 * @author Joannick Gardize
 *
 */
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface ForceInject {
	boolean injectParent() default false;
}