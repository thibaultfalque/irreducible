/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.heaps package.
 *
 * It contains the HeapFactory, which enables to easily create parameterized instances of
 * Heap.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.heaps;

import java.util.Comparator;
import java.util.function.Supplier;

import fr.univartois.cril.approximation.util.collections.MapInt;

/**
 * The HeapFactory enables to easily create parameterized instances of {@link Heap}.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class HeapFactory {

    /**
     * Disables instantiation.
     */
    private HeapFactory() {
        throw new AssertionError("No HeapFactory instances for you!");
    }

    /**
     * Creates a new heap with a descending order.
     * The elements are ordered using their natural order.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * 
     * @return The created heap.
     */
    public static <E extends Heapable & Comparable<? super E>> Heap<E> newMaximumHeap(
            int capacity) {
        return newHeap(capacity, (a, b) -> a.compareTo(b), HeapOrder.DESCENDING);
    }

    /**
     * Creates a new heap with an ascending order.
     * The elements are ordered using their natural order.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * 
     * @return The created heap.
     */
    public static <E extends Heapable & Comparable<? super E>> Heap<E> newMinimumHeap(
            int capacity) {
        return newHeap(capacity, (a, b) -> a.compareTo(b), HeapOrder.ASCENDING);
    }

    /**
     * Creates a new heap with a descending order.
     * The elements are ordered using the given comparator.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param comparator The comparator used to compare the elements in the heap.
     * 
     * @return The created heap.
     */
    public static <E extends Heapable> Heap<E> newMaximumHeap(int capacity,
            Comparator<? super E> comparator) {
        return newHeap(capacity, comparator, HeapOrder.DESCENDING);
    }

    /**
     * Creates a new heap with an ascending order.
     * The elements are ordered using the given comparator.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param comparator The comparator used to compare the elements in the heap.
     * 
     * @return The created heap.
     */
    public static <E extends Heapable> Heap<E> newMinimumHeap(int capacity,
            Comparator<? super E> comparator) {
        return newHeap(capacity, comparator, HeapOrder.ASCENDING);
    }

    /**
     * Creates a new heap with a descending order.
     * The elements are ordered using their natural order.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param cache The supplier for the cache to use to retrieve elements in constant
     *        time.
     * 
     * @return The created heap.
     */
    public static <E extends Comparable<? super E>> Heap<E> newMaximumHeap(int capacity,
            Supplier<MapInt<? super E>> cache) {
        return newHeap(capacity, cache, (a, b) -> a.compareTo(b), HeapOrder.DESCENDING);
    }

    /**
     * Creates a new heap with an ascending order.
     * The elements are ordered using their natural order.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param cache The supplier for the cache to use to retrieve elements in constant
     *        time.
     * 
     * @return The created heap.
     */
    public static <E extends Comparable<? super E>> Heap<E> newMinimumHeap(int capacity,
            Supplier<MapInt<? super E>> cache) {
        return newHeap(capacity, cache, (a, b) -> a.compareTo(b), HeapOrder.ASCENDING);
    }

    /**
     * Creates a new heap with a descending order.
     * The elements are ordered using the given comparator.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param cache The supplier for the cache to use to retrieve elements in constant
     *        time.
     * @param comparator The comparator used to compare the elements in the heap.
     * 
     * @return The created heap.
     */
    public static <E> Heap<E> newMaximumHeap(int capacity, Supplier<MapInt<? super E>> cache,
            Comparator<? super E> comparator) {
        return newHeap(capacity, cache, comparator, HeapOrder.DESCENDING);
    }

    /**
     * Creates a new heap with an ascending order.
     * The elements are ordered using the given comparator.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param cache The supplier for the cache to use to retrieve elements in constant
     *        time.
     * @param comparator The comparator used to compare the elements in the heap.
     * 
     * @return The created heap.
     */
    public static <E> Heap<E> newMinimumHeap(int capacity, Supplier<MapInt<? super E>> cache,
            Comparator<? super E> comparator) {
        return newHeap(capacity, cache, comparator, HeapOrder.ASCENDING);
    }

    /**
     * Creates a new heap ordered using the given comparator.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param comparator The comparator used to compare the elements in the heap.
     * @param state The state of the heap to create, used to specify which of the minimum
     *        or maximum must be available in constant time from the heap.
     * 
     * @return The created heap.
     */
    private static <E extends Heapable> Heap<E> newHeap(int capacity,
            Comparator<? super E> comparator, HeapOrder state) {
        return new Heap<>(capacity, comparator, state, Heapable::getIndex, Heapable::setIndex);
    }

    /**
     * Creates a new heap ordered using the given comparator.
     * 
     * @param <E> The type of the elements in the heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param cache The supplier for the cache to use to retrieve elements in constant
     *        time.
     * @param comparator The comparator used to compare the elements in the heap.
     * @param state The state of the heap to create, used to specify which of the minimum
     *        or maximum must be available in constant time from the heap.
     * 
     * @return The created heap.
     */
    private static <E> Heap<E> newHeap(int capacity, Supplier<MapInt<? super E>> cache,
            Comparator<? super E> comparator, HeapOrder state) {
        MapInt<? super E> effectiveCache = cache.get();

        return new Heap<>(capacity, comparator, state,
                element -> effectiveCache.safeGet(element).orElse(-1),
                (element, index) -> {
                    if (index < 0) {
                        // The element is to be removed.
                        effectiveCache.remove(element);

                    } else {
                        // The element is to be put.
                        effectiveCache.put(element, index);
                    }
                });
    }

}
