/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the SubIndexedCollection, which provides an unmodifiable view of a sub-part
 * of an IndexedCollection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import java.util.Objects;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;

/**
 * The SubIndexedCollection provides an unmodifiable view of a sub-part of an
 * {@link IndexedCollection}.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class SubIndexedCollection<E> extends AbstractIndexedCollection<E> {

    /**
     * The collection from which this sub-collection is extracted.
     */
    private final IndexedCollection<E> collection;

    /**
     * The start index of this sub-collection (inclusive).
     */
    private final int start;

    /**
     * The end index of this sub-collection (exclusive).
     */
    private final int end;

    /**
     * Creates a new SubIndexedCollection.
     * 
     * @param collection The collection from which the sub-collection is extracted.
     * @param start The start index of the sub-collection (inclusive).
     * @param end The end index of the sub-collection (exclusive).
     * 
     * @throws NullPointerException If {@code collection} is {@code null}.
     * @throws IndexOutOfBoundsException If the given range is out of the bounds of
     *         the collection.
     */
    public SubIndexedCollection(IndexedCollection<E> collection, int start, int end) {
        this.collection = Objects.requireNonNull(collection);
        this.start = Objects.checkFromToIndex(start, end, collection.size());
        this.end = end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return end - start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return end == start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#get(int)
     */
    @Override
    public E get(int index) {
        return collection.get(Objects.checkIndex(index, size()) + start);
    }

}
