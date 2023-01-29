/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the SwapOperationState, which allows to enable or disable the swap
 * operation on an IndexedCollection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;

/**
 * The SwapOperationState allows to enable or disable the {@code swap} operation
 * on an {@link IndexedCollection}.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public enum SwapOperationState {

    /**
     * The state which enables the {@code swap} operation.
     */
    ENABLED {
        
        @Override
        public void swap(IndexedCollection<?> collection, int first, int second) {
            collection.swap(first, second);
        }
        
    },

    /**
     * The state which disables the {@code swap} operation.
     */
    DISABLED {
        
        @Override
        public void swap(IndexedCollection<?> collection, int first, int second) {
            throw new UnsupportedOperationException();
        }
        
    };

    /**
     * Swaps the elements at the two given indices in the given collection.
     * 
     * @param collection The collection in which to swap elements.
     * @param first The index of the first element.
     * @param second The index of the second element.
     * 
     * @throws IndexOutOfBoundsException If one of the indices is out of the bounds of
     *         the collection.
     * @throws UnsupportedOperationException If this state disables the {@code swap}
     *         operation or if the collection itself does not support it.
     */
    public abstract void swap(IndexedCollection<?> collection, int first, int second);

}
