package com.sharkecs.builder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import com.sharkecs.annotation.Inject;
import com.sharkecs.annotation.SkipInjection;
import com.sharkecs.util.ReflectionUtils;

public class Injector {

	private Set<Class<?>> autoInjectTypes;

	public Injector() {
		autoInjectTypes = new HashSet<>();
	}

	public void addAutoInjectType(Class<?> type) {
		autoInjectTypes.add(type);
	}

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
			if (!injectByName(object, field, registrations) && !injectByGenericType(object, field, registrations)) {
				inject(object, field, null, registrations);
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
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
			        | NoSuchMethodException e1) {
				throw new EngineConfigurationException("No setter found to inject the field " + field);
			}
		} else {
			return false;
		}
	}

	private boolean isEligibleField(Field field, boolean injectAllFields) {
		return !field.isAnnotationPresent(SkipInjection.class)
		        && (injectAllFields || field.isAnnotationPresent(Inject.class));
	}

	private boolean isAutoInjectType(Class<?> type) {
		return autoInjectTypes.stream().anyMatch(t -> t.isAssignableFrom(type))
		        || type.isAnnotationPresent(Inject.class);
	}

}
