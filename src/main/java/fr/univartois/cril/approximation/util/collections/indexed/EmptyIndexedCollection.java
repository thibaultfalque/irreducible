/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the EmptyIndexedCollection, which is an unmodifiable implementation of
 * IndexedCollection which does not contain any element.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import java.util.Collection;
import java.util.Objects;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;

/**
 * The EmptyIndexedCollection is an unmodifiable implementation of
 * {@link IndexedCollection} which does not contain any element.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class EmptyIndexedCollection<E> extends AbstractIndexedCollection<E> {

    /**
     * The single instance of this class.
     * Thanks to type erasure, it can be used for any type.
     */
    private static final IndexedCollection<?> INSTANCE = new EmptyIndexedCollection<>();

    /**
     * Creates a new EmptyIndexedCollection.
     */
    private EmptyIndexedCollection() {
        // Nothing to do: Singleton Design Pattern.
    }

    /**
     * Gives the single instance of EmptyIndexedCollection.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @return An immutable empty collection.
     */
    @SuppressWarnings("unchecked")
    public static <E> IndexedCollection<E> instance() {
        return (IndexedCollection<E>) INSTANCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#clear()
     */
    @Override
    public void clear() {
        // No-op on an empty collection.
    }

    @Override
    public void fastClear() {
        // No-op on an empty collection.
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#get(int)
     */
    @Override
    public E get(int index) {
        throw new IndexOutOfBoundsException(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return c.isEmpty();
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
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#
     * subCollection(int, int)
     */
    @Override
    public IndexedCollection<E> subCollection(int start, int end) {
        Objects.checkFromToIndex(start, end, 0);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#toArray()
     */
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length > 0) {
            a[0] = null;
        }
        return a;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        return (object instanceof IndexedCollection) && ((IndexedCollection<?>) object).isEmpty();
    }

}
