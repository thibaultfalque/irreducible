/*
 * This file is a part of the fr.univartois.cril.orpheus.utils package.
 *
 * It contains the CollectionFactory, which provides factory methods instantiating the
 * collections used in the solver.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;
import fr.univartois.cril.approximation.util.collections.MapDouble;
import fr.univartois.cril.approximation.util.collections.MapInt;
import fr.univartois.cril.approximation.util.collections.Stack;
import fr.univartois.cril.approximation.util.collections.StackAdapter;
import fr.univartois.cril.approximation.util.collections.dynamic.DynamicallyManagedIndexedCollection;
import fr.univartois.cril.approximation.util.collections.indexed.ArrayIndexedCollection;
import fr.univartois.cril.approximation.util.collections.indexed.EmptyIndexedCollection;
import fr.univartois.cril.approximation.util.collections.indexed.RepeatedIndexedCollection;
import fr.univartois.cril.approximation.util.collections.indexed.UnmodifiableIndexedCollection;
import fr.univartois.cril.approximation.util.collections.maps.ArrayBasedMap;
import fr.univartois.cril.approximation.util.collections.maps.ArrayBasedMapDouble;
import fr.univartois.cril.approximation.util.collections.maps.ArrayBasedMapInt;
import fr.univartois.cril.approximation.util.collections.maps.MapToSetAdapter;

/**
 * The CollectionFactory provides factory methods instantiating the collections used in
 * the solver.
 *
 * They are designed to return the most efficient known implementation.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class CollectionFactory {

    /**
     * Disables instantiation.
     */
    private CollectionFactory() {
        throw new AssertionError("No CollectionFactory instances for you!");
    }

    /**
     * Creates a new collection.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @return The created collection.
     */
    public static <E> Collection<E> newCollection() {
        return newIndexedCollection();
    }

    /**
     * Creates a new indexed collection.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @return The created indexed collection.
     */
    public static <E> IndexedCollection<E> newIndexedCollection() {
        return new DynamicallyManagedIndexedCollection<>(new ArrayIndexedCollection<>());
    }

    /**
     * Creates a new indexed collection.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param size The initial size of the collection.
     * 
     * @return The created indexed collection.
     */
    public static <E> IndexedCollection<E> newIndexedCollection(int size) {
        return new DynamicallyManagedIndexedCollection<>(new ArrayIndexedCollection<>(size));
    }

    /**
     * Creates a new indexed collection initially filled with the given value.
     * This is just a convenient method for initializing the collection, and the
     * resulting collection can be modified afterwards.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param size The initial size of the collection.
     * @param padding The value to fill the collection with.
     * 
     * @return The created indexed collection.
     * 
     * @throws IllegalArgumentException If {@code size < 0}.
     */
    public static <E> IndexedCollection<E> newIndexedCollectionFilledWith(int size, E padding) {
        return new DynamicallyManagedIndexedCollection<>(
                new ArrayIndexedCollection<>(size, padding));
    }

    /**
     * Creates an immutable indexed collection containing no element.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @return An immutable empty indexed collection.
     */
    public static <E> IndexedCollection<E> emptyIndexedCollection() {
        return EmptyIndexedCollection.instance();
    }

    /**
     * Creates an immutable indexed collection containing exactly the given element.
     * 
     * @param <E> The type of the element in the collection.
     * 
     * @param element The single element of the collection.
     * 
     * @return An immutable indexed collection containing {@code element} as single
     *         element.
     */
    public static <E> IndexedCollection<E> singletonIndexedCollection(E element) {
        return repeatedIndexedCollection(1, element);
    }

    /**
     * Creates an immutable indexed collection containing the same element, repeated
     * multiple times.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param size The size of the collection.
     * @param repeated The element to repeat.
     * 
     * @return An immutable indexed collection containing exactly {@code size} times the
     *         {@code repeated} element.
     * 
     * @throws IllegalArgumentException If {@code size <= 0}.
     * @throws NullPointerException If {@code repeated} is {@code null}.
     */
    public static <E> IndexedCollection<E> repeatedIndexedCollection(int size, E repeated) {
        return new RepeatedIndexedCollection<>(size, repeated);
    }

    /**
     * Creates an indexed collection containing the given elements.
     * The resulting collection does not support adding or removing elements.
     * The {@code swap} operation is supported.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param elements The elements of the collection.
     * 
     * @return The created indexed collection.
     */
    @SafeVarargs
    public static <E> IndexedCollection<E> asIndexedCollection(E... elements) {
        return ArrayIndexedCollection.of(elements);
    }

    /**
     * Creates a new indexed collection, and adds all the elements contained in the given
     * collection to the newly created collection.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param elements The elements of the collection.
     * 
     * @return The created indexed collection.
     */
    public static <E> IndexedCollection<E> asIndexedCollection(Collection<? extends E> elements) {
        IndexedCollection<E> collection = newIndexedCollection(elements.size());
        collection.addAll(elements);
        return collection;
    }

    /**
     * Creates an indexed collection view of the given list.
     * The resulting view is an <i>object adapter</i> of the list, so any change to the
     * list will alter the view, and vice-versa.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param list The list to adapt.
     * 
     * @return The created indexed collection.
     */
    public static <E> IndexedCollection<E> adaptsAsIndexedCollection(List<E> list) {
        return new ArrayIndexedCollection<>(list);
    }

    /**
     * Creates an unmodifiable view of the given indexed collection.
     * All {@code add}, {@code set}, {@code remove} and {@code swap} operations
     * are unsupported by the returned view.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param collection The collection to create an unmodifiable view for.
     * 
     * @return The created collection.
     */
    public static <E> IndexedCollection<E> unmodifiableIndexedCollection(
            IndexedCollection<E> collection) {
        return new UnmodifiableIndexedCollection<>(collection);
    }

    /**
     * Creates a new list.
     * 
     * @param <E> The type of the elements in the list.
     * 
     * @return The created list.
     */
    public static <E> List<E> newList() {
        return new LinkedList<>();
    }

    /**
     * Creates a new list.
     * 
     * @param <E> The type of the elements in the list.
     * 
     * @param size The initial size of the list.
     * 
     * @return The created list.
     */
    public static <E> List<E> newList(int size) {
        return new ArrayList<>(size);
    }

    /**
     * Creates a new stack.
     * 
     * @param <E> The type of the elements in the stack.
     * 
     * @return The created stack.
     */
    public static <E> Stack<E> newStack() {
        return new StackAdapter<>(newDeque());
    }

    /**
     * Creates a new queue.
     * 
     * @param <E> The type of the elements in the queue.
     * 
     * @return The created queue.
     */
    public static <E> Queue<E> newQueue() {
        return newDeque();
    }

    /**
     * Creates a new deque.
     * 
     * @param <E> The type of the elements in the deque.
     * 
     * @return The created deque.
     */
    public static <E> Deque<E> newDeque() {
        return new LinkedList<>();
    }

    /**
     * Creates a new set.
     * 
     * @param <E> The type of the elements in the set.
     * 
     * @return The created set.
     */
    public static <E> Set<E> newSet() {
        return new HashSet<>();
    }

    /**
     * Creates a new sorted set.
     * 
     * @param <E> The type of the elements in the set.
     * 
     * @return The created sorted set.
     */
    public static <E extends Comparable<? super E>> SortedSet<E> newSortedSet() {
        return new TreeSet<>();
    }

    /**
     * Creates a new sorted set.
     * 
     * @param <E> The type of the elements in the set.
     * 
     * @param comparator The comparator to use to sort the elements in the set.
     * 
     * @return The created sorted set.
     */
    public static <E> SortedSet<E> newSortedSet(Comparator<? super E> comparator) {
        return new TreeSet<>(comparator);
    }

    /**
     * Adapts the given map to consider it as a set.
     * 
     * @param <E> The type of the elements in the set.
     * 
     * @param map The map to adapt.
     * 
     * @return A set adapting the given map.
     */
    public static <E> Set<E> asSet(Map<E, E> map) {
        return new MapToSetAdapter<>(map);
    }

    /**
     * Adapts the map supplied by the given supplier to consider it as a set.
     * 
     * @param <E> The type of the elements in the set.
     * 
     * @param mapSupplier The supplier for the map to adapt.
     * 
     * @return A set adapting the supplied map.
     */
    public static <E> Set<E> asSet(Supplier<Map<E, E>> mapSupplier) {
        return new MapToSetAdapter<>(mapSupplier.get());
    }

    /**
     * Creates a new map.
     * 
     * @param <K> The type of the keys of the map.
     * @param <V> The type of the values of the map.
     * 
     * @return The created map.
     */
    public static <K, V> Map<K, V> newMap() {
        return new HashMap<>();
    }

    /**
     * Creates a new map.
     * 
     * @param <K> The type of the keys of the map.
     * @param <V> The type of the values of the map.
     * 
     * @param keyClass The class of the keys.
     * @param keyToIndex The function to use to compute the index of a key.
     * @param indexToKey The function to use to retrieve the key from an index.
     * @param capacity The capacity of the map.
     * 
     * @return The created map.
     */
    public static <K, V> Map<K, V> newMap(Class<K> keyClass, ToIntFunction<K> keyToIndex,
            IntFunction<K> indexToKey, int capacity) {
        return new ArrayBasedMap<>(keyClass, keyToIndex, indexToKey, capacity);
    }

    /**
     * Creates a new map having {@code int} values.
     * 
     * @param <K> The type of the keys of the map.
     * 
     * @param keyClass The class of the keys.
     * @param keyToIndex The function to use to compute the index of a key.
     * @param indexToKey The function to use to retrieve the key from an index.
     * @param capacity The capacity of the map.
     * 
     * @return The created map.
     */
    public static <K> MapInt<K> newMapInt(Class<K> keyClass, ToIntFunction<K> keyToIndex,
            IntFunction<K> indexToKey, int capacity) {
        return new ArrayBasedMapInt<>(keyClass, keyToIndex, indexToKey, capacity);
    }

    /**
     * Creates a new map having {@code double} values.
     * 
     * @param <K> The type of the keys of the map.
     * 
     * @param keyClass The class of the keys.
     * @param keyToIndex The function to use to compute the index of a key.
     * @param indexToKey The function to use to retrieve the key from an index.
     * @param capacity The capacity of the map.
     * 
     * @return The created map.
     */
    public static <K> MapDouble<K> newMapDouble(Class<K> keyClass, ToIntFunction<K> keyToIndex,
            IntFunction<K> indexToKey, int capacity) {
        return new ArrayBasedMapDouble<>(keyClass, keyToIndex, indexToKey, capacity);
    }

}
