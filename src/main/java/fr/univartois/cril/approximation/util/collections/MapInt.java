/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections package.
 *
 * It contains the MapInt, which is a map that may have any type as keys, and int as
 * values.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.IntBinaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * The MapInt is a map that may have any type as keys, and {@code int} as values.
 * 
 * @param <K> The type of the keys.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public interface MapInt<K> extends Iterable<MapInt.Entry<K>> {

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return The number of key-value mappings in this map.
     */
    int size();

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings.
     */
    boolean isEmpty();

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     * 
     * More formally, returns {@code true} if and only if this map contains a mapping for
     * a key {@code k} such that {@code Objects.equals(key, k)}.
     * There can be at most one such mapping.
     *
     * @param key The key whose presence in this map is to be tested.
     * 
     * @return {@code true} if this map contains a mapping for the specified key.
     * 
     * @throws ClassCastException If the key is of an inappropriate type for this map.
     * @throws NullPointerException If the specified key is {@code null} and this map
     *         does not permit {@code null} keys.
     */
    boolean containsKey(Object key);

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value.
     * 
     * More formally, returns {@code true} if and only if this map contains at least
     * one mapping to a value {@code v} such that {@code value == v}.
     * This operation will probably require time linear in the map size for most
     * implementations of the {@code MapInt} interface.
     *
     * @param value The value whose presence in this map is to be tested.
     * 
     * @return {@code true} if this map maps one or more keys to the specified value.
     */
    boolean containsValue(int value);

    /**
     * Returns the value to which the specified key is mapped.
     *
     * More formally, if this map contains a mapping from a key {@code k} to a value
     * {@code v} such that {@code Objects.equals(key, k)}, then this method returns
     * {@code v}; otherwise, the result is <b>undefined</b>.
     * 
     * Use {@link #safeGet(Object)} when not sure about whether the key has a mapping
     * in this map.
     *
     * @param key The key whose associated value is to be returned.
     * 
     * @return The value to which the specified key is mapped.
     * 
     * @throws ClassCastException If the key is of an inappropriate type for this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     *         
     * @see #safeGet(Object)
     */
    int get(Object key);

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code OptionalInt.empty()} if this map contains no mapping for the key.
     *
     * More formally, if this map contains a mapping from a key {@code k} to a value
     * {@code v} such that {@code Objects.equals(key, k)}, then this method returns
     * {@code OptionalInt.of(v)}; otherwise it returns {@code OptionalInt.empty()}.
     * There can be at most one such mapping.
     *
     * @param key The key whose associated value is to be returned.
     * 
     * @return The value to which the specified key is mapped, or
     *         {@code OptionalInt.empty()} if this map contains no mapping for the key.
     * 
     * @throws ClassCastException If the key is of an inappropriate type for this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     */
    OptionalInt safeGet(Object key);

    /**
     * Returns the value to which the specified key is mapped, or {@code defaultValue} if
     * this map contains no mapping for the key.
     *
     * @param key The key whose associated value is to be returned.
     * @param defaultValue The default mapping of the key.
     * 
     * @return The value to which the specified key is mapped, or {@code defaultValue} if
     *         this map contains no mapping for the key.
     * 
     * @throws ClassCastException If the key is of an inappropriate type for this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     *
     * @implSpec The default implementation makes no guarantees about synchronization or
     *           atomicity properties of this method.
     *           Any implementation providing atomicity guarantees must override this
     *           method and document its concurrency properties.
     */
    default int getOrDefault(Object key, int defaultValue) {
        return safeGet(key).orElse(defaultValue);
    }

    /**
     * Associates the specified value with the specified key in this map (optional
     * operation).
     * If the map previously contained a mapping for the key, the old value is replaced by
     * the specified value.
     * A map {@code m} is said to contain a mapping for a key {@code k} if and only if
     * {@link #containsKey(Object) m.containsKey(k)} would return {@code true}.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * 
     * @return The previous value associated with {@code key}, or
     *         {@code OptionalInt.empty()} if there was no mapping for {@code key}.
     *
     * @throws UnsupportedOperationException If the {@code put} operation is not supported
     *         by this map.
     * @throws ClassCastException If the class of the specified key prevents it from being
     *         stored in this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     * @throws IllegalArgumentException If some property of the specified key or value
     *         prevents it from being stored in this map.
     */
    OptionalInt put(K key, int value);

    /**
     * If the specified key is not already associated with a value, associates it with the
     * given value and returns {@code OptionalInt.empty()}, else returns the current
     * value.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * 
     * @return The previous value associated with the specified key, or
     *         {@code OptionalInt.empty()} if there was no mapping for the key.
     *
     * @throws UnsupportedOperationException If the {@code put} operation is not supported
     *         by this map.
     * @throws ClassCastException If the key is of an inappropriate type for this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     * @throws IllegalArgumentException If some property of the specified key or value
     *         prevents it from being stored in this map.
     *
     * @implSpec The default implementation makes no guarantees about synchronization or
     *           atomicity properties of this method.
     *           Any implementation providing atomicity guarantees must override this
     *           method and document its concurrency properties.
     */
    default OptionalInt putIfAbsent(K key, int value) {
        var oldValue = safeGet(key);
        if (!oldValue.isPresent()) {
            oldValue = put(key, value);
        }
        return oldValue;
    }

    /**
     * Copies all of the mappings from the specified map to this map (optional operation).
     * The effect of this call is equivalent to that of calling {@link #put(Object, int)
     * put(k, v)} on this map once for each mapping from key {@code k} to value {@code v}
     * in the specified map.
     * The behavior of this operation is undefined if the specified map is modified while
     * the operation is in progress.
     *
     * @param map The mappings to be stored in this map.
     * 
     * @throws UnsupportedOperationException If the {@code putAll} operation is not
     *         supported by this map.
     * @throws ClassCastException If the class of a key in the specified map prevents it
     *         from being stored in this map.
     * @throws NullPointerException If a key in the specified map is {@code null} and this
     *         map does not permit {@code null} keys.
     * @throws IllegalArgumentException If some property of a key or value in the
     *         specified map prevents it from being stored in this map.
     */
    void putAll(MapInt<? extends K> map);

    /**
     * If the specified key is not already associated with a value, attempts to compute
     * its value using the given mapping function and enters it into this map.
     *
     * <p>
     * If the mapping function itself throws an (unchecked) exception, the exception is
     * rethrown, and no mapping is recorded.
     * The most common usage is to construct a new object serving as an initial mapped
     * value or memoized result.
     * 
     * <p>
     * The mapping function should not modify this map during computation.
     *
     * @param key The key with which the specified value is to be associated.
     * @param mappingFunction The mapping function to compute a value.
     * 
     * @return The current (existing or computed) value associated with the specified key.
     * 
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys or if the {@code mappingFunction} is
     *         {@code null}.
     * @throws UnsupportedOperationException If the {@code put} operation is not supported
     *         by this map.
     * @throws ClassCastException If the class of the specified key prevents it from being
     *         stored in this map.
     * @throws IllegalArgumentException If some property of the specified key or value
     *         prevents it from being stored in this map.
     * 
     * @implSpec The default implementation makes no guarantees about detecting if the
     *           mapping function modifies this map during computation and, if
     *           appropriate, reporting an error.
     *           Non-concurrent implementations should override this method and, on a
     *           best-effort basis, throw a {@code ConcurrentModificationException} if it
     *           is detected that the mapping function modifies this map during
     *           computation.
     *           Concurrent implementations should override this method and, on a
     *           best-effort basis, throw an {@code IllegalStateException} if it is
     *           detected that the mapping function modifies this map during computation
     *           and as a result computation would never complete.
     *
     *           <p>
     *           The default implementation makes no guarantees about synchronization or
     *           atomicity properties of this method.
     *           Any implementation providing atomicity guarantees must override this
     *           method and document its concurrency properties.
     */
    default int computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        var oldValue = safeGet(key);

        if (oldValue.isPresent()) {
            return oldValue.getAsInt();
        }

        int newValue = mappingFunction.applyAsInt(key);
        put(key, newValue);
        return newValue;
    }

    /**
     * If the specified key is not already associated with a value, associates it with
     * the given value.
     * Otherwise, replaces the associated value with the results of the given remapping
     * function.
     * This method may be of use when combining multiple mapped values for a key.
     *
     * <pre>
     * {@code map.merge(key, value, Integer::sum)}
     * </pre>
     *
     * <p>
     * If the remapping function itself throws an (unchecked) exception, the exception is
     * rethrown, and the current mapping is left unchanged.
     *
     * <p>
     * The remapping function should not modify this map during computation.
     *
     * @param key The key with which the resulting value is to be associated.
     * @param value The value to be merged with the existing value associated with the key
     *        or, if no existing value is associated with the key, to be associated with
     *        the key.
     * @param remappingFunction The remapping function to recompute a value if present.
     * 
     * @return The new value associated with the specified key.
     * 
     * @throws UnsupportedOperationException If the {@code put} operation is not supported
     *         by this map.
     * @throws ClassCastException If the class of the specified key prevents it from being
     *         stored in this map.
     * @throws IllegalArgumentException If some property of the specified key or value
     *         prevents it from being stored in this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys or if the {@code remappingFunction} is
     *         {@code null}.
     *
     * @implSpec The default implementation makes no guarantees about detecting if the
     *           remapping function modifies this map during computation and, if
     *           appropriate, reporting an error.
     *           Non-concurrent implementations should override this method and, on a
     *           best-effort basis, throw a {@code ConcurrentModificationException} if it
     *           is detected that the remapping function modifies this map during
     *           computation.
     *           Concurrent implementations should override this method and, on a
     *           best-effort basis, throw an {@code IllegalStateException} if it is
     *           detected that the remapping function modifies this map during computation
     *           and as a result computation would never complete.
     *
     *           <p>
     *           The default implementation makes no guarantees about synchronization or
     *           atomicity properties of this method.
     *           Any implementation providing atomicity guarantees must override this
     *           method and document its concurrency properties.
     */
    default int merge(K key, int value, IntBinaryOperator remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        var oldValue = safeGet(key);
        int newValue = oldValue.isPresent()
                ? remappingFunction.applyAsInt(oldValue.getAsInt(), value)
                : value;
        put(key, newValue);
        return newValue;
    }

    /**
     * Removes the mapping for a key from this map if it is present (optional operation).
     * More formally, if this map contains a mapping from key {@code k} to value {@code v}
     * such that {@code Objects.equals(key, k)}, that mapping is removed.
     * The map can contain at most one such mapping.
     *
     * <p>
     * Returns the value to which this map previously associated the key,
     * or {@code OptionalInt.empty()} if the map contained no mapping for the key.
     *
     * <p>
     * The map will not contain a mapping for the specified key once the call returns.
     *
     * @param key The key whose mapping is to be removed from the map.
     * 
     * @return The previous value associated with {@code key}, or
     *         {@code OptionalInt.empty()} if there was no mapping for {@code key}.
     *
     * @throws UnsupportedOperationException If the {@code remove} operation is not
     *         supported by this map.
     * @throws ClassCastException If the key is of an inappropriate type for this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     */
    OptionalInt remove(Object key);

    /**
     * Removes the entry for the specified key only if it is currently mapped to the
     * specified value.
     *
     * @param key The key with which the specified value is associated.
     * @param value The value expected to be associated with the specified key.
     * 
     * @return If the value was removed.
     * 
     * @throws UnsupportedOperationException If the {@code remove} operation is not
     *         supported by this map.
     * @throws ClassCastException If the key is of an inappropriate type for this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     * 
     * @implSpec The default implementation makes no guarantees about synchronization or
     *           atomicity properties of this method.
     *           Any implementation providing atomicity guarantees must override this
     *           method and document its concurrency properties.
     */
    default boolean remove(Object key, int value) {
        var oldValue = safeGet(key);
        if (oldValue.isPresent() && (oldValue.getAsInt() == value)) {
            remove(key);
            return true;
        }

        return false;
    }

    /**
     * Replaces the entry for the specified key only if it is currently mapped to some
     * value.
     *
     * @param key The key with which the specified value is associated.
     * @param value The value to be associated with the specified key.
     * 
     * @return The previous value associated with the specified key, or
     *         {@code OptionalInt.empty()} if there was no mapping for the key.
     *
     * @throws UnsupportedOperationException If the {@code put} operation is not supported
     *         by this map.
     * @throws ClassCastException If the class of the specified key or value prevents it
     *         from being stored in this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     * @throws IllegalArgumentException If some property of the specified key or value
     *         prevents it from being stored in this map.
     *
     * @implSpec The default implementation makes no guarantees about synchronization or
     *           atomicity properties of this method.
     *           Any implementation providing atomicity guarantees must override this
     *           method and document its concurrency properties.
     */
    default OptionalInt replace(K key, int value) {
        var currentValue = safeGet(key);
        if (currentValue.isPresent()) {
            put(key, value);
            return currentValue;
        }

        return OptionalInt.empty();
    }

    /**
     * Replaces the entry for the specified key only if currently mapped to the specified
     * value.
     *
     * @param key The key with which the specified value is associated.
     * @param oldValue The value expected to be associated with the specified key.
     * @param newValue The value to be associated with the specified key.
     * 
     * @return If the value was replaced.
     * 
     * @throws UnsupportedOperationException If the {@code put} operation is not supported
     *         by this map.
     * @throws ClassCastException If the class of the specified key prevents it from being
     *         stored in this map.
     * @throws NullPointerException If the specified key is {@code null} and this map does
     *         not permit {@code null} keys.
     * @throws IllegalArgumentException If some property of the specified key or value
     *         prevents it from being stored in this map.
     * 
     * @implSpec The default implementation makes no guarantees about synchronization or
     *           atomicity properties of this method.
     *           Any implementation providing atomicity guarantees must override this
     *           method and document its concurrency properties.
     */
    default boolean replace(K key, int oldValue, int newValue) {
        var currentValue = safeGet(key);
        if (!currentValue.isPresent() || (currentValue.getAsInt() != oldValue)) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    /**
     * Removes all of the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     *
     * @throws UnsupportedOperationException If the {@code clear} operation is not
     *         supported by this map.
     */
    void clear();

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.
     * If the map is modified while an iteration over the set is in progress (except
     * through the iterator's own {@code remove} operation), the results of the iteration
     * are undefined.
     * The set supports element removal, which removes the corresponding mapping from the
     * map, via the {@code remove}, {@code removeAll}, {@code retainAll}, and
     * {@code clear} operations.
     * It does not support the {@code add} or {@code addAll} operations.
     *
     * @return A set view of the keys contained in this map.
     */
    Set<K> keySet();

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.
     * If the map is modified while an iteration over the set is in progress (except
     * through the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the iterator) the results of
     * the iteration are undefined.
     * The set supports element removal, which removes the corresponding mapping from the
     * map, via the {@code remove}, {@code removeAll}, {@code retainAll} and {@code clear}
     * operations.
     * It does not support the {@code add} or {@code addAll} operations.
     *
     * @return A set view of the mappings contained in this map.
     */
    Set<Entry<K>> entrySet();

    /**
     * A map-int entry (key-value pair).
     * The {@code MapInt.entrySet} method returns a collection-view of the map, whose
     * elements are of this class.
     * {@code MapInt.Entry} objects are valid <i>only</i> for the duration of the
     * iteration; more formally, the behavior of a map entry is undefined if the backing
     * map has been modified after the entry was returned by the iterator, except through
     * the {@code setValue} operation on the map entry.
     * 
     * @param <K> The type of the key.
     *
     * @see MapInt#entrySet()
     */
    interface Entry<K> {

        /**
         * Returns the key corresponding to this entry.
         *
         * @return The key corresponding to this entry.
         * 
         * @throws IllegalStateException Implementations may, but are not required to,
         *         throw this exception if the entry has been removed from the backing
         *         map.
         */
        K getKey();

        /**
         * Returns the value corresponding to this entry.
         * If the mapping has been removed from the backing map (by the iterator's
         * {@code remove} operation), the results of this call are undefined.
         *
         * @return The value corresponding to this entry.
         * 
         * @throws IllegalStateException Implementations may, but are not required to,
         *         throw this exception if the entry has been removed from the backing
         *         map.
         */
        int getValue();

        /**
         * Replaces the value corresponding to this entry with the specified value
         * (optional operation, writes through to the map).
         * The behavior of this call is undefined if the mapping has already been removed
         * from the map (by the iterator's {@code remove} operation).
         *
         * @param value The new value to be stored in this entry.
         * 
         * @return The old value corresponding to the entry.
         * 
         * @throws UnsupportedOperationException If the {@code put} operation is not
         *         supported by the backing map.
         * @throws IllegalArgumentException If some property of this value prevents it
         *         from being stored in the backing map.
         * @throws IllegalStateException Implementations may, but are not required to,
         *         throw this exception if the entry has been removed from the backing
         *         map.
         */
        int setValue(int value);

        /**
         * Returns the hash code value for this map entry.
         * The hash code of a map entry {@code e} is defined to be:
         * 
         * <pre>
         * ((e.getKey() == null) ? 0 : e.getKey().hashCode()) ^ Integer.hashCode(e.getValue())
         * </pre>
         * 
         * This ensures that {@code e1.equals(e2)} implies that
         * {@code e1.hashCode() == e2.hashCode()} for any two Entries {@code e1} and
         * {@code e2}, as required by the general contract of {@code Object.hashCode}.
         *
         * @return The hash code value for this map entry.
         * 
         * @see Object#hashCode()
         * @see Object#equals(Object)
         * @see #equals(Object)
         */
        @Override
        int hashCode();

        /**
         * Compares the specified object with this entry for equality.
         * Returns {@code true} if the given object is also a map entry and the two
         * entries represent the same mapping.
         * More formally, two entries {@code e1} and {@code e2} represent the same mapping
         * if:
         * 
         * <pre>
         * Objects.equals(e1.getKey(), e2.getKey()) 
         *         &amp;&amp; (e1.getValue() == e2.getValue());
         * </pre>
         * 
         * This ensures that the {@code equals} method works properly across different
         * implementations of the {@code MapInt.Entry} interface.
         *
         * @param object The object to be compared for equality with this map entry.
         * 
         * @return {@code true} if the specified object is equal to this map entry.
         */
        @Override
        boolean equals(Object object);

    }

    /**
     * Performs the given action for each entry in this map until all entries have been
     * processed or the action throws an exception.
     * Unless otherwise specified by the implementing class, actions are performed in the
     * order of entry set iteration, if an iteration order is specified.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry.
     * 
     * @throws NullPointerException If the specified action is {@code null}.
     *
     * @implSpec The default implementation makes no guarantees about synchronization or
     *           atomicity properties of this method.
     *           Any implementation providing atomicity guarantees must override this
     *           method and document its concurrency properties.
     */
    default void forEach(ObjIntConsumer<? super K> action) {
        Objects.requireNonNull(action);
        for (Entry<K> entry : entrySet()) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Returns the hash code value for this map.
     * The hash code of a map is defined to be the sum of the hash codes of each entry in
     * the map's {@code entrySet()} view.
     * This ensures that {@code m1.equals(m2)} implies that
     * {@code m1.hashCode() == m2.hashCode()} for any two maps {@code m1} and {@code m2},
     * as required by the general contract of {@link Object#hashCode}.
     *
     * @return The hash code value for this map.
     * 
     * @see MapInt.Entry#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    @Override
    int hashCode();

    /**
     * Compares the specified object with this map for equality.
     * Returns {@code true} if the given object is also a map and the two maps represent
     * the same mappings.
     * More formally, two maps {@code m1} and {@code m2} represent the same mappings if
     * {@code m1.entrySet().equals(m2.entrySet())}.
     * This ensures that the {@code equals} method works properly across different
     * implementations of the {@code MapInt} interface.
     *
     * @param object The object to be compared for equality with this map.
     * 
     * @return {@code true} if the specified object is equal to this map.
     */
    @Override
    boolean equals(Object object);

}
