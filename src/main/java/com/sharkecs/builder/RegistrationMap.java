package com.sharkecs.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A map storing elements by element type and key.
 * 
 * @author Joannick Gardize
 *
 */
public class RegistrationMap {

	private List<Object> list = new ArrayList<>();
	private Map<Class<?>, Map<Object, Object>> map = new IdentityHashMap<>();

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
		Map<Object, Object> typeMap = map.computeIfAbsent(type, t -> new HashMap<>());
		if (typeMap.put(key, o) != null) {
			throw new EngineConfigurationException("Duplicate registration: [type = " + type + ", key = " + key + "]");
		}
		list.add(o);
	}

	/**
	 * put or get the object associated with the given type and key.
	 * 
	 * @param <T>           the type of the object
	 * @param type          the type to associate the object with
	 * @param key           the key to associate the object with
	 * @param valueSupplier the supplier of object used if not present
	 * @return the object retrieved or newly created
	 */
	@SuppressWarnings("unchecked")
	public <T> T computeIfAbsent(Class<T> type, Object key, Supplier<T> valueSupplier) {
		Map<Object, Object> typeMap = map.computeIfAbsent(type, t -> new HashMap<>());
		return (T) typeMap.computeIfAbsent(key, k -> valueSupplier.get());
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
		return (T) map.getOrDefault(type, Collections.emptyMap()).get(key);
	}

	/**
	 * Returns an entry set or key/value pairs of the given object type.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Set<Entry<Object, T>> entrySet(Class<T> type) {
		return (Set) map.getOrDefault(type, Collections.emptyMap()).entrySet();
	}

	/**
	 * @param type
	 * @return the number of entry of the given type
	 */
	public int typeCount(Class<?> type) {
		return map.getOrDefault(type, Collections.emptyMap()).size();
	}

	/**
	 * Iterates over all values of all types.
	 * 
	 * @param action
	 */
	public void forEach(Consumer<Object> action) {
		list.forEach(action);
	}
}
