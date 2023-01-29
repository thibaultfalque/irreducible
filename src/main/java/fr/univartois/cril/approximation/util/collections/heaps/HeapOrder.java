/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.heaps package.
 *
 * It contains the HeapOrder, which enables to specify the order in which the elements of
 * a heap must be removed.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.heaps;

import java.util.Comparator;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;

/**
 * The HeapOrder enables to specify the order in which the elements of a heap must be
 * removed.
 * 
 * In particular, this enumeration allows to specify which of the minimum or maximum
 * element can be retrieved in constant time from the heap (that is, which element is
 * on the top of the heap).
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
enum HeapOrder {

    /**
     * The HeapOrder for removing elements in ascending order.
     * This order makes the minimum of the heap retrievable in constant time.
     */
    ASCENDING(cmp -> -cmp, Heap::bubbleDown, Heap::bubbleUp),

    /**
     * The HeapOrder for removing elements in descending order.
     * This order makes the maximum of the heap retrievable in constant time.
     */
    DESCENDING(cmp -> cmp, Heap::bubbleUp, Heap::bubbleDown);

    /**
     * The operator to apply to the {@code int} value returned by
     * {@link Comparator#compare(Object, Object)} to compare the objects appropriately
     * according to this order.
     */
    private final IntUnaryOperator compare;

    /**
     * The function to call to update the heap when a value at a given index is increased.
     */
    private final ObjIntConsumer<Heap<?>> increase;

    /**
     * The function to call to update the heap when a value at a given index is decreased.
     */
    private final ObjIntConsumer<Heap<?>> decrease;

    /**
     * Creates a new HeapOrder.
     * 
     * @param compare The operator to apply to the {@code int} value returned by
     *        {@link Comparator#compare(Object, Object)} to compare the objects
     *        appropriately according to the order.
     * @param increase The function to call to update the heap when a value at a given index
     *        is increased.
     * @param decrease The function to call to update the heap when a value at a given index
     *        is decreased.
     */
    private HeapOrder(IntUnaryOperator compare, ObjIntConsumer<Heap<?>> increase,
            ObjIntConsumer<Heap<?>> decrease) {
        this.compare = compare;
        this.increase = increase;
        this.decrease = decrease;
    }

    /**
     * Compares the two given values using the given comparator, and returns an integer
     * representing the relative order of these values according to this order.
     * 
     * @param <E> The type of the objects to compare.
     * 
     * @param comparator The comparator to use to perform the comparison.
     * @param first The first element to compare.
     * @param second The second element to compare.
     * 
     * @return An integer representing the relative order of {@code first} and
     *         {@code second} according to this order.
     */
    <E> int compare(Comparator<E> comparator, E first, E second) {
        return compare.applyAsInt(comparator.compare(first, second));
    }

    /**
     * Updates the position of the value at the given index in the heap when this value
     * is increased.
     * 
     * @param heap The heap to update.
     * @param index The index of the value that has been increased.
     */
    void increase(Heap<?> heap, int index) {
        increase.accept(heap, index);
    }

    /**
     * Updates the position of the value at the given index in the heap when this value
     * is decreased.
     * 
     * @param heap The heap to update.
     * @param index The index of the value that has been decreased.
     */
    void decrease(Heap<?> heap, int index) {
        decrease.accept(heap, index);
    }

}
