/*
 * Copyright 2024 Joannick Gardize
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sharkhendrix.sharkecs.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;

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
                } else if (typeArgument instanceof ParameterizedType && ((ParameterizedType) typeArgument).getRawType() instanceof Class<?>) {
                    return (Class<?>) ((ParameterizedType) typeArgument).getRawType();

                }
            }
        }
        return null;
    }

    public static Method getSetter(Field field) throws NoSuchMethodException {
        return field.getDeclaringClass().getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
    }

    public static void forEachAssignableTypes(Class<?> type, Consumer<Class<?>> action) {
        Class<?> currentType = type;
        while (currentType != null && currentType != Object.class) {
            action.accept(currentType);
            for (Class<?> interfaceType : currentType.getInterfaces()) {
                action.accept(interfaceType);
            }
            currentType = currentType.getSuperclass();
        }
    }

    /**
     * Get the annotation of the given {@code annotationType} to the given
     * {@code type}, Going up to superclass until the annotation is found. Returns
     * null if the annotation has not been found.
     *
     * @param <A>
     * @param type
     * @param annotationType
     * @return
     */
    public static <A extends Annotation> A getAnnotationOnSuperclass(Class<?> type, Class<A> annotationType) {
        Class<?> currentType = type;
        A annotation = type.getAnnotation(annotationType);
        while (annotation == null && currentType != null && currentType != Object.class) {
            currentType = currentType.getSuperclass();
            annotation = type.getAnnotation(annotationType);
        }
        return annotation;
    }
}
