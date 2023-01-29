/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.heaps package.
 *
 * It contains the Heap, which is an implementation of a heap allowing to retrieve any of
 * its element efficiently, thanks to index functions.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.heaps;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * The Heap is an implementation of a heap allowing to retrieve any of its element
 * efficiently, thanks to index functions.
 * 
 * The value of an element in the heap can also be updated, so that the heap remains
 * consistent even if updated values alter the order of the elements, providing that
 * the heap is told about this update by using {@link #increase(Object)} or
 * {@link #decrease(Object)}.
 * 
 * @param <E> The type of the elements in the heap.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class Heap<E> extends AbstractCollection<E> implements Queue<E> {

    /**
     * The elements of this heap.
     */
    private E[] elements;
    
    /**
     * The comparator used to compare the elements in this heap.
     */
    private final Comparator<? super E> comparator;

    /**
     * The state of this heap, used to specify which of the minimum or maximum must
     * be available in constant time from this heap.
     */
    private final HeapOrder state;

    /**
     * The function used to get the index of an element in this heap.
     */
    private final ToIntFunction<E> getIndex;

    /**
     * The function used to set the index of an element in this heap.
     */
    private final ObjIntConsumer<E> setIndex;
    
    /**
     * The size of this heap.
     */
    private int size;

    /**
     * Creates a new Heap.
     * 
     * @param capacity The initial capacity of the heap.
     * @param comparator The comparator to use to compare the elements in the heap.
     * @param state The state of this heap, used to specify which of the minimum or
     *        maximum must be available in constant time from this heap.
     * @param getIndex The function used to get the index of an element in the heap.
     * @param setIndex The function used to set the index of an element in the heap.
     */
    @SuppressWarnings("unchecked")
    Heap(int capacity, Comparator<? super E> comparator, HeapOrder state,
            ToIntFunction<E> getIndex, ObjIntConsumer<E> setIndex) {
        this.elements = (E[]) new Object[capacity];
        this.comparator = comparator;
        this.state = state;
        this.getIndex = getIndex;
        this.setIndex = setIndex;
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return size;
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#add(java.lang.Object)
     */
    @Override
    public boolean add(E element) {
        if (size == elements.length) {
            // Doubling the capacity of this heap.
            elements = Arrays.copyOf(elements, elements.length << 1);
        }
        
        // Putting the element in the heap.
        this.elements[size] = element;
        setIndex.accept(element, size);
        bubbleUp(size);
        size++;
        return true;
    }

    /**
     * Tells this heap that the value of an element has been increased.
     * 
     * @param element The element whose value has been increased.
     */
    public void increase(E element) {
        int index = getIndex.applyAsInt(element);
        if (index >= 0) {
            state.increase(this, index);
        }
    }

    /**
     * Tells this heap that the value of an element has been decreased.
     * 
     * @param element The element whose value has been decreased.
     */
    public void decrease(E element) {
        int index = getIndex.applyAsInt(element);
        if (index >= 0) {
            state.decrease(this, index);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#offer(java.lang.Object)
     */
    @Override
    public boolean offer(E e) {
        return add(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#remove()
     */
    @Override
    public E remove() {
        E polled = poll();
        if (polled == null) {
            throw new NoSuchElementException();
        }
        return polled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#poll()
     */
    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }

        // Removing the head.
        E head = elements[0];
        elements[0] = null;
        setIndex.accept(head, -1);
        size--;
        
        if (!isEmpty()) {
            // Updating the head while preserving heap property.
            elements[0] = elements[size];
            setIndex.accept(elements[0], 0);
            elements[size()] = null;
            bubbleDown(0);
        }

        return head;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#element()
     */
    @Override
    public E element() {
        E peek = peek();
        if (peek == null) {
            throw new NoSuchElementException();
        }
        return peek;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#peek()
     */
    @Override
    public E peek() {
        return elements[0];
    }

    /**
     * Bubbles-up an element so that it gets an higher position in the heap according to
     * the associated comparator.
     * 
     * @param index The index of the element to bubble-up.
     */
    void bubbleUp(int index) {
        if (index != 0) {
            int parentIndex = parent(index);
            E element = elements[index];
            E parent = elements[parentIndex];

            if (compare(element, parent) > 0) {
                // Heap property is broken: swapping
                elements[index] = parent;
                setIndex.accept(parent, index);
                elements[parentIndex] = element;
                setIndex.accept(element, parentIndex);
                bubbleUp(parentIndex);
            }
        }
    }

    /**
     * Bubbles-down an element so that it gets a lower position in the heap according to
     * the associated comparator.
     * 
     * @param index The index of the element to bubble-down.
     */
    void bubbleDown(int index) {
        int right = rightChild(index);
        int left = leftChild(index);

        // Retrieving which child is greater than the other.
        int maxIndex = -1;
        if ((left < size) && (right < size)) {
            maxIndex = compare(elements[left], elements[right]) > 0 ? left : right;

        } else if (left < size) {
            maxIndex = left;
        }

        if ((maxIndex >= 0) && (compare(elements[maxIndex], elements[index]) > 0)) {
            // Heap property is broken: swapping
            E tmp = elements[index];
            elements[index] = elements[maxIndex];
            setIndex.accept(elements[index], index);
            elements[maxIndex] = tmp;
            setIndex.accept(elements[maxIndex], maxIndex);
            bubbleDown(maxIndex);
        }
    }
    
    /**
     * Delegates to both {{@link #comparator} and {@link #state} the comparison of the 
     * two given elements.
     * 
     * @param first The first element to compare.
     * @param second The second element to compare.
     * 
     * @return An integer representing the relative order of {@code first} and 
     *         {@code second}.
     */
    private int compare(E first, E second) {
        return state.compare(comparator, first, second);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {

            /**
             * The current index in the heap.
             */
            private int currentIndex;

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#hasNext()
             */
            @Override
            public boolean hasNext() {
                return currentIndex < size();
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#next()
             */
            @Override
            public E next() {
                if (hasNext()) {
                    return elements[currentIndex++];
                }
                throw new NoSuchElementException();
            }

        };
    }

    /**
     * Gives the index of the parent of the element at the given index.
     * 
     * @param index The index to get the parent of.
     * 
     * @return The index of the parent of the element at the given index.
     */
    private static int parent(int index) {
        return (index - 1) >> 1;
    }

    /**
     * Gives the index of the right child of the element at the given index.
     * 
     * @param index The index to get the right child of.
     * 
     * @return The index of the right child of the element at the given index.
     */
    private static int rightChild(int index) {
        return (index << 1) + 2;
    }

    /**
     * Gives the index of the left child of the element at the given index.
     * 
     * @param index The index to get the left child of.
     * 
     * @return The index of the left child of the element at the given index.
     */
    private static int leftChild(int index) {
        return (index << 1) + 1;
    }

}
