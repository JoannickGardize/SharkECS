package com.sharkecs.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtils {

	private ReflectionUtils() {
		throw new UnsupportedOperationException();
	}

	public static Class<?> getFirstGenericTypeArgument(Type type) {
		if (type instanceof ParameterizedType) {
			Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
			if (typeArguments.length > 0) {
				Type typeArgument = typeArguments[0];
				if (typeArgument instanceof Class) {
					return (Class<?>) typeArgument;
				} else if (typeArgument instanceof ParameterizedType
				        && ((ParameterizedType) typeArgument).getRawType() instanceof Class<?>) {
					return (Class<?>) ((ParameterizedType) typeArgument).getRawType();

				}
			}
		}
		return null;
	}

	public static Method getSetter(Field field) throws NoSuchMethodException {
		return field.getDeclaringClass().getMethod(
		        "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
	}
}
