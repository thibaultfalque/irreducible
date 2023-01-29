/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the RepeatedIndexedCollection, which is an indexed collection consisting in
 * the same element repeated multiple times.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import java.util.Objects;

/**
 * The RepeatedIndexedCollection is an indexed collection consisting in the same element
 * repeated multiple times.
 * 
 * The repeated element cannot be {@code null}.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class RepeatedIndexedCollection<E> extends AbstractIndexedCollection<E> {

    /**
     * The size of the collection.
     */
    private final int size;

    /**
     * The element which is repeated in this collection.
     */
    private final E repeated;

    /**
     * Creates a new RepeatedIndexedCollection.
     * 
     * @param size The size of the collection.
     * @param repeated The element to repeat.
     * 
     * @throws IllegalArgumentException If {@code size <= 0}.
     * @throws NullPointerException If {@code repeated} is {@code null}.
     */
    public RepeatedIndexedCollection(int size, E repeated) {
        if (size <= 0) {
            throw new IllegalArgumentException("Cannot create collection of non-positive size!");
        }

        this.size = size;
        this.repeated = Objects.requireNonNull(repeated);
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
     * @see
     * fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#swap
     * (int, int)
     */
    @Override
    public void swap(int first, int second) {
        // Provided that first and second are within the bounds of this collection,
        // swapping will not alter this collection, as all elements are the same.
        Objects.checkIndex(first, size);
        Objects.checkIndex(second, size);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#get(int)
     */
    @Override
    public E get(int index) {
        Objects.checkIndex(index, size);
        return repeated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#
     * indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object object) {
        if (repeated.equals(object)) {
            return 0;
        }

        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return repeated.equals(o);
    }

}
