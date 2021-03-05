package com.sharkecs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.sharkecs.annotation.WithAll;
import com.sharkecs.annotation.WithAny;
import com.sharkecs.annotation.Without;

class Aspect {

    private Set<Class<?>> withAll;
    private Set<Class<?>> withAny;
    private Set<Class<?>> without;

    public Aspect(Class<?> annotatedType) {
        WithAll withAllAnnotation = annotatedType.getAnnotation(WithAll.class);
        withAll = withAllAnnotation != null ? new HashSet<>(Arrays.asList(withAllAnnotation.value())) : null;
        WithAny withAnyAnnotation = annotatedType.getAnnotation(WithAny.class);
        withAny = withAnyAnnotation != null ? new HashSet<>(Arrays.asList(withAnyAnnotation.value())) : null;
        Without withoutAnnotation = annotatedType.getAnnotation(Without.class);
        without = withoutAnnotation != null ? new HashSet<>(Arrays.asList(withoutAnnotation.value())) : null;
    }

    public boolean matches(Set<Class<?>> componentTypes) {
        if (withAll != null) {
            for (Class<?> type : withAll) {
                if (!componentTypes.contains(type)) {
                    return false;
                }
            }
        }
        if (without != null) {
            for (Class<?> type : without) {
                if (componentTypes.contains(type)) {
                    return false;
                }
            }
        }
        if (withAny != null) {
            return anyMatches(componentTypes);
        }
        return true;
    }

    private boolean anyMatches(Set<Class<?>> componentTypes) {
        for (Class<?> type : withAny) {
            if (componentTypes.contains(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + withAll.hashCode();
        result = prime * result + withAny.hashCode();
        result = prime * result + without.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Aspect)) {
            return false;
        }
        Aspect other = (Aspect) obj;
        return Objects.equals(withAll, other.withAll) && Objects.equals(withAny, other.withAny)
                && Objects.equals(without, other.without);
    }

}
