/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the AddOperationState, which allows to enable or disable the add
 * operation on an IndexedCollection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;

/**
 * The AddOperationState allows to enable or disable the {@code add} operation
 * on an {@link IndexedCollection}.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public enum AddOperationState {

    /**
     * The state which enables the {@code add} operation.
     */
    ENABLED {

        @Override
        public <E> boolean add(IndexedCollection<E> collection, E element) {
            return collection.add(element);
        }

    },
    
    /**
     * The state which disables the {@code add} operation.
     */
    DISABLED {

        @Override
        public <E> boolean add(IndexedCollection<E> collection, E element) {
            throw new UnsupportedOperationException();
        }

    };

    /**
     * Adds the given element to the given collection.
     * 
     * @param <E> The type of the elements in the collection.
     * 
     * @param collection The collection in which to add the element.
     * @param element The element to add.
     * 
     * @return Whether the element has been added.
     * 
     * @throws UnsupportedOperationException If this state disables the {@code add}
     *         operation or if the collection itself does not support it.
     */
    public abstract <E> boolean add(IndexedCollection<E> collection, E element);
    
}
