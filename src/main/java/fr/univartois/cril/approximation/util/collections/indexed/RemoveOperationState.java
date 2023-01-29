/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the RemoveOperationState, which allows to enable or disable the remove
 * operation on an IndexedCollection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;

/**
 * The RemoveOperationState allows to enable or disable the {@code remove} operation
 * on an {@link IndexedCollection}.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public enum RemoveOperationState {

    /**
     * The state which enables the {@code remove} operation.
     */
    ENABLED {

        @Override
        public <E> E remove(IndexedCollection<E> collection, int index) {
            return collection.remove(index);
        }

        @Override
        public boolean remove(IndexedCollection<?> collection, Object object) {
            return collection.remove(object);
        }

    },

    /**
     * The state which disables the {@code remove} operation.
     */
    DISABLED {

        @Override
        public <E> E remove(IndexedCollection<E> collection, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(IndexedCollection<?> collection, Object object) {
            throw new UnsupportedOperationException();
        }

    };

    /**
     * Removes the element at the given index from the given collection.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param collection The collection from which to remove an element.
     * @param index The index of the element to remove.
     * 
     * @return The element that has been removed.
     * 
     * @throws IndexOutOfBoundsException If the index is out of the bounds of the
     *         collection.
     * @throws UnsupportedOperationException If this state disables the {@code remove}
     *         operation or if the collection itself does not support it.
     */
    public abstract <E> E remove(IndexedCollection<E> collection, int index);
    
    /**
     * Removes the given element from the given collection.
     * 
     * @param collection The collection from which to remove an element.
     * @param object The object to remove.
     * 
     * @return Whether the element has been removed.
     * 
     * @throws UnsupportedOperationException If this state disables the {@code remove}
     *         operation or if the collection itself does not support it.
     */
    public abstract boolean remove(IndexedCollection<?> collection, Object object);
    
}
