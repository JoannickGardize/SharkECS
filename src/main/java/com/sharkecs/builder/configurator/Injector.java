package com.sharkecs.builder.configurator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import com.sharkecs.annotation.Inject;
import com.sharkecs.annotation.SkipInjection;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.builder.EngineConfigurationException;
import com.sharkecs.builder.RegistrationMap;
import com.sharkecs.util.ReflectionUtils;

/**
 * <p>
 * Configurator wiring the eligible fields of registered objects of a
 * {@link RegistrationMap} between them.
 * <p>
 * A field is eligible if one of the following condition is met:
 * <ul>
 * <li>The declaring class is annotated with {@link Inject}
 * <li>The field is annotated with {@link Inject}
 * <li>The declaring class is assignable from one of the registered auto inject
 * type via {@link #addAutoInjectType(Class)}
 * </ul>
 * If the field or its declaring class is annotated with {@link SkipInjection},
 * injection will always be skipped.
 * <p>
 * When a field is eligible, the injector will look the {@link RegistrationMap}
 * for an adequate object. If there is objects registered with the exact same
 * type (no <i>is assignable from</i> check), it will choose one by checking its
 * registration keys by following these rules, in this priority order:
 * <ul>
 * <li>a String key exactly matching the field name
 * <li>a Class key matching the first generic type of the field's type
 * <li>the empty key (null)
 * </ul>
 * <p>
 * Injection is done via setter methods, if a field is eligible and a registered
 * object has been found for it, but the setter method is missing or not
 * visible, an {@link EngineConfigurationException} is thrown.
 * <p>
 * If a field is eligible but no registered object has been found for it,
 * nothing happens, this can be changed by
 * {@link Injector#setFailWhenNotFound(boolean)}.
 * <p>
 * By default, the fields of parent classes is not checked, use
 * {@link Inject#injectParent()} on the class to change that.
 * 
 * @author Joannick Gardize
 *
 */
public class Injector implements Configurator {

	private Set<Class<?>> autoInjectTypes = new HashSet<>();
	private boolean failWhenNotFound = false;

	/**
	 * Adds the given type as auto-inject type. Objects assignable from this type
	 * will be injected without the need of marking them with {@link Inject}.
	 * 
	 * @param type the auto-inject type to add
	 */
	public void addAutoInjectType(Class<?> type) {
		autoInjectTypes.add(type);
	}

	public void removeAutoInjectType(Class<?> type) {
		autoInjectTypes.remove(type);
	}

	/**
	 * @param failWhenNotFound if true, injection will fail with an
	 *                         {@link EngineConfigurationException} when no object
	 *                         is found for a field.
	 */
	public void setFailWhenNotFound(boolean failWhenNotFound) {
		this.failWhenNotFound = failWhenNotFound;
	}

	@Override
	public void configure(EngineBuilder engineBuilder) {
		engineBuilder.getRegistrations().forEach(o -> inject(o, engineBuilder.getRegistrations()));
	}

	/**
	 * Inject the eligible fields of the given object, using the given registration
	 * map.
	 * 
	 * @param object        the object to inject its eligible fields
	 * @param registrations the registration map to use for injection
	 */
	public void inject(Object object, RegistrationMap registrations) {
		inject(object.getClass(), object, registrations);
	}

	private void inject(Class<?> type, Object object, RegistrationMap registrations) {
		Inject inject = type.getAnnotation(Inject.class);
		if (inject != null && inject.injectParent()) {
			inject(type.getSuperclass(), object, registrations);
		}
		if (type.isAnnotationPresent(SkipInjection.class)) {
			return;
		}
		boolean injectAllFields = isAutoInjectType(type);
		for (Field field : type.getDeclaredFields()) {
			if (!isEligibleField(field, injectAllFields) || registrations.typeCount(field.getType()) == 0) {
				continue;
			}
			if (!injectByName(object, field, registrations) && !injectByGenericType(object, field, registrations) && !inject(object, field, null, registrations)
					&& failWhenNotFound) {
				throw new EngineConfigurationException("No registered object found for field " + field);
			}
		}
	}

	private boolean injectByName(Object object, Field field, RegistrationMap registrations) {
		return inject(object, field, field.getName(), registrations);
	}

	private boolean injectByGenericType(Object object, Field field, RegistrationMap registrations) {
		Class<?> argumentType = ReflectionUtils.getFirstGenericTypeArgument(field.getGenericType());
		if (argumentType != null) {
			return inject(object, field, argumentType, registrations);
		} else {
			return false;
		}
	}

	private boolean inject(Object object, Field field, Object key, RegistrationMap registrations) {
		Object registration = registrations.get(field.getType(), key);
		if (registration != null) {
			try {
				ReflectionUtils.getSetter(field).invoke(object, registration);
				return true;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
				throw new EngineConfigurationException("Missing public setter to inject the field " + field, e);
			}
		} else {
			return false;
		}
	}

	private boolean isEligibleField(Field field, boolean injectAllFields) {
		return !field.isAnnotationPresent(SkipInjection.class) && (injectAllFields || field.isAnnotationPresent(Inject.class));
	}

	private boolean isAutoInjectType(Class<?> type) {
		return autoInjectTypes.stream().anyMatch(t -> t.isAssignableFrom(type)) || type.isAnnotationPresent(Inject.class);
	}
}
