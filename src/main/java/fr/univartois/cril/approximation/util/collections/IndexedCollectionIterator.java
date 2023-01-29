/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections package.
 *
 * It contains the IndexedCollectionIterator, which adds some features to a classical
 * iterator when it is used on an IndexedCollection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections;

import java.util.Iterator;

/**
 * The IndexedCollectionIterator adds some features to a classical iterator when it is
 * used on an {@link IndexedCollection}.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public interface IndexedCollectionIterator<E> extends Iterator<E> {

    /**
     * Replaces the current element by the given one.
     * 
     * @param e The new element.
     * 
     * @throws UnsupportedOperationException If this iterator does not support replacement, or
     *         if the collection does not support the {@code set} operation.
     * 
     * @implSpec The default implementation throws an {@link UnsupportedOperationException}.
     */
    default void replace(E e) {
        throw new UnsupportedOperationException();
    }
    
}
