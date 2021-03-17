package com.sharkecs.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.sharkecs.util.ReflectionUtils;

/**
 * A map storing elements by element type and key. Also able to retrieve all
 * registered objects assignable from a given type.
 * 
 * @author Joannick Gardize
 *
 */
public class RegistrationMap {

	private List<Object> list = new ArrayList<>();
	private Map<Class<?>, Map<Object, Object>> byDeclaredTypeAndKey = new IdentityHashMap<>();
	private Map<Class<?>, List<Object>> byAssignableType = new IdentityHashMap<>();

	/**
	 * Add the given object with a null key.
	 * 
	 * @param o the object to put
	 */
	public void put(Object o) {
		put(null, o);
	}

	/**
	 * Add the given object o with the given key.
	 * 
	 * @param key the key to associate the object with
	 * @param o   the object to put
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void put(Object key, Object o) {
		put((Class) o.getClass(), key, o);
	}

	/**
	 * Add the given object o with the given key, using the given type as type
	 * entry.
	 * 
	 * @param <T>  the type of the object
	 * @param type the type to associate the object with
	 * @param key  the key to associate the object with
	 * @param o    the object to put
	 */
	public <T> void put(Class<? super T> type, Object key, T o) {
		Map<Object, Object> typeMap = byDeclaredTypeAndKey.computeIfAbsent(type, t -> new HashMap<>());
		if (typeMap.put(key, o) != null) {
			throw new EngineConfigurationException("Duplicate registration: [type = " + type + ", key = " + key + "]");
		}
		addAssignableTypes(o);
		list.add(o);
	}

	private <T> void addAssignableTypes(T o) {
		ReflectionUtils.forEachAssignableTypes(o.getClass(), t -> byAssignableType.computeIfAbsent(t, t2 -> new ArrayList<>()).add(o));
	}

	/**
	 * put or get the object associated with the given type and key.
	 * 
	 * @param <T>           the type of the object
	 * @param type          the declared type to associate the object with
	 * @param key           the key to associate the object with
	 * @param valueSupplier the supplier of object used if not present
	 * @return the object retrieved or newly created
	 */
	@SuppressWarnings("unchecked")
	public <T> T computeIfAbsent(Class<? super T> type, Object key, Supplier<T> valueSupplier) {
		Map<Object, Object> typeMap = byDeclaredTypeAndKey.computeIfAbsent(type, t -> new HashMap<>());

		return (T) typeMap.computeIfAbsent(key, k -> {
			T value = valueSupplier.get();
			addAssignableTypes(value);
			return value;
		});
	}

	/**
	 * Get the object of the given type with a null key. Throws an
	 * {@link EngineConfigurationException} if not found.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T> T getOrFail(Class<T> type) {
		return getOrFail(type, null);
	}

	/**
	 * Get the object of the given type with the given key. Throws an
	 * {@link EngineConfigurationException} if not found.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T> T getOrFail(Class<T> type, Object key) {
		T o = get(type, key);
		if (o == null) {
			throw new EngineConfigurationException("unknown registration: [type = " + type + ", key = " + key + "]");
		}
		return o;
	}

	/**
	 * Get the object of the given type with a null key, or null if not found.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T> T get(Class<T> type) {
		return get(type, null);
	}

	/**
	 * Get the object of the given type with the given key, or null if not found.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, Object key) {
		return (T) byDeclaredTypeAndKey.getOrDefault(type, Collections.emptyMap()).get(key);
	}

	/**
	 * Returns an entry set of key/value pairs of the given object type.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Set<Entry<Object, T>> entrySet(Class<T> type) {
		return (Set) Collections.unmodifiableSet(byDeclaredTypeAndKey.getOrDefault(type, Collections.emptyMap()).entrySet());
	}

	/**
	 * @param type
	 * @return the number of entry of the given type
	 */
	public int typeCount(Class<?> type) {
		return byDeclaredTypeAndKey.getOrDefault(type, Collections.emptyMap()).size();
	}

	/**
	 * Iterates over all registered objects assignable to the given type.
	 * 
	 * @param type
	 * @param action
	 */
	public <T> void forEachAssignableFrom(Class<T> type, Consumer<T> action) {
		getAllAssignableFrom(type).forEach(action);
	}

	/**
	 * returns a collection of all registered objects assignable to the given type.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getAllAssignableFrom(Class<T> type) {
		return (Collection<T>) Collections.unmodifiableCollection(byAssignableType.getOrDefault(type, Collections.emptyList()));
	}

	/**
	 * Retrieve any registered object assignable from the given type.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAnyAssignableFrom(Class<T> type) {
		List<Object> typeList = byAssignableType.get(type);
		return typeList != null && !typeList.isEmpty() ? (T) typeList.get(0) : null;
	}

	/**
	 * Iterates over all registered objects.
	 * 
	 * @param action
	 */
	public void forEach(Consumer<Object> action) {
		list.forEach(action);
	}

	/**
	 * @return all registered objects
	 */
	public List<Object> all() {
		return Collections.unmodifiableList(list);
	}
}
