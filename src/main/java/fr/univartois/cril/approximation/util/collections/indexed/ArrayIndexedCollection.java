/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the ArrayIndexedCollection, which implements an IndexedCollection using an
 * array.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;

/**
 * The ArrayIndexedCollection implements an {@link IndexedCollection} using an array.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class ArrayIndexedCollection<E> extends AbstractIndexedCollection<E> {

    /**
     * The default size of the underlying array.
     */
    private static final int DEFAULT_SIZE = 5;

    /**
     * The elements of this collection.
     */
    private E[] elements;

    /**
     * The size of this collection.
     */
    private int size = 0;

    /**
     * Creates a new ArrayIndexedCollection with the default initial size.
     */
    public ArrayIndexedCollection() {
        this(DEFAULT_SIZE);
    }

    /**
     * Creates a new ArrayIndexedCollection with the given initial size.
     * 
     * @param initialSize The initial size of the collection.
     */
    @SuppressWarnings("unchecked")
    public ArrayIndexedCollection(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException();
        }
        this.elements = (E[]) new Object[initialSize];
    }

    /**
     * Creates a new ArrayIndexedCollection with the given initial size and
     * filled with the given padding.
     * 
     * @param initialSize The initial size of the collection.
     * @param padding The element to fill the collection with.
     */
    public ArrayIndexedCollection(int initialSize, E padding) {
        this(initialSize);
        Arrays.fill(this.elements, padding);
        this.size = initialSize;
    }

    /**
     * Creates a new ArrayIndexedCollection initialized with the elements of the given
     * collection.
     * 
     * @param collection The collection to initialize the ArrayIndexedCollection with.
     * 
     * @throws NullPointerException If {@code collection} is {@code null}.
     */
    public ArrayIndexedCollection(Collection<? extends E> collection) {
        this(collection.size());
        addAll(collection);
    }

    /**
     * Creates a new ArrayIndexedCollection.
     * 
     * @param elements The elements of the collection.
     */
    private ArrayIndexedCollection(E[] elements) {
        this.elements = elements;
    }

    /**
     * Creates an indexed collection made of the given elements.
     * The resulting collection does not support adding or removing elements.
     * The {@code swap} operation is supported.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param elements The elements of the collection.
     * 
     * @return An indexed collection made of the given elements.
     */
    @SafeVarargs
    public static <E> IndexedCollection<E> of(E... elements) {
        ArrayIndexedCollection<E> collection = new ArrayIndexedCollection<>(elements);
        collection.size = elements.length;

        return new UnmodifiableIndexedCollection<>(collection, AddOperationState.DISABLED, 
                RemoveOperationState.DISABLED, SwapOperationState.ENABLED);
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
            // The array must be resized to store the new element.
            elements = Arrays.copyOf(elements, elements.length << 1);
        }
        
        elements[size++] = element;
        return true;
    }

    /* 
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#get(int)
     */
    @Override
    public E get(int index) {
        return elements[Objects.checkIndex(index, size)];
    }

    /* 
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#swap(int, int)
     */
    @Override
    public void swap(int first, int second) {
        Objects.checkIndex(first, size);
        Objects.checkIndex(second, size);
        
        E tmp = elements[first];
        elements[first] = elements[second];
        elements[second] = tmp;
    }

    /* 
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#set(int, java.lang.Object)
     */
    @Override
    public E set(int index, E e) {
        Objects.checkIndex(index, size);
        
        E previous = elements[index];
        elements[index] = e;
        
        return previous;
    }

    /* 
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#remove(int)
     */
    @Override
    public E remove(int index) {
        Objects.checkIndex(index, size);
        
        E removed = elements[index];
        elements[index] = elements[size - 1];
        elements[size - 1] = null;
        
        if ((--size) < (elements.length / 3)) {
            // The array should be resized to avoid to use too much memory.
            elements = Arrays.copyOf(elements, elements.length >> 1);
        }
        
        return removed;
    }
    
    /* 
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#trimToSize()
     */
    @Override
    public void trimToSize() {
        elements = Arrays.copyOf(elements, size);
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#clear()
     */
    @Override
    public void clear() {
        size = 0;
        Arrays.fill(elements, null);
    }
    
    /* (non-Javadoc)
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#fastClear()
     */
    @Override
    public void fastClear() {
        size = 0;
    }
}
