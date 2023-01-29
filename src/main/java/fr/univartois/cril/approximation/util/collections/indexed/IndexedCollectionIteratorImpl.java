/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the IndexedCollectionIterator, which enables to iterate over an
 * IndexedCollection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import java.util.NoSuchElementException;
import java.util.Objects;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;
import fr.univartois.cril.approximation.util.collections.IndexedCollectionIterator;

/**
 * The IndexedCollectionIterator enables to iterate over an {@link IndexedCollection}.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class IndexedCollectionIteratorImpl<E> implements IndexedCollectionIterator<E> {

    /**
     * The collection to iterate over.
     */
    private final IndexedCollection<E> collection;

    /**
     * The current index in the collection.
     */
    private int currentIndex;

    /**
     * Creates a new IndexedCollectionIterator.
     * 
     * @param collection The collection to iterate over.
     * 
     * @throws NullPointerException If {@code collection} is {@code null}.
     */
    public IndexedCollectionIteratorImpl(IndexedCollection<E> collection) {
        this.collection = Objects.requireNonNull(collection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return currentIndex < collection.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public E next() {
        if (hasNext()) {
            return collection.get(currentIndex++);
        }

        throw new NoSuchElementException("No more elements!");
    }
    
    /* 
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollectionIterator#replace(java.lang.Object)
     */
    @Override
    public void replace(E e) {
        collection.set(currentIndex - 1, e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        collection.remove(currentIndex - 1);
        currentIndex--;
    }

}
