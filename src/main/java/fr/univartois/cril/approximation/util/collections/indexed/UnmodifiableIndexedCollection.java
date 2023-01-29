/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the UnmodifiableIndexedCollection, which is a proxy for an
 * IndexedCollection that provides an unmodifiable view of this collection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;

/**
 * The UnmodifiableIndexedCollection is a proxy for an {@link IndexedCollection} that
 * provides an unmodifiable view of this collection.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class UnmodifiableIndexedCollection<E> extends AbstractIndexedCollection<E> {

    /**
     * The collection for which this collection is an unmodifiable view.
     */
    private final IndexedCollection<E> collection;

    /**
     * The state which enables or disables the {@code add} operation on the collection.
     */
    private final AddOperationState addState;

    /**
     * The state which enables or disables the {@code remove} operation on the collection.
     */
    private final RemoveOperationState removeState;

    /**
     * The state which enables or disables the {@code swap} operation on the collection.
     */
    private final SwapOperationState swapState;

    /**
     * Creates a new UnmodifiableIndexedCollection.
     * All {@code add}, {@code remove} and {@code swap} operations are unsupported.
     * 
     * @param collection The collection for which the new collection is an unmodifiable view.
     */
    public UnmodifiableIndexedCollection(IndexedCollection<E> collection) {
        this(collection, AddOperationState.DISABLED, RemoveOperationState.DISABLED,
                SwapOperationState.DISABLED);
    }

    /**
     * Creates a new UnmodifiableIndexedCollection, with a fine-grained control on which
     * operations are enabled and which are disabled.
     * 
     * @param collection The collection for which the new collection is an unmodifiable view.
     * @param addState The state which enables or disables the {@code add} operation on
     *        the collection.
     * @param removeState The state which enables or disables the {@code remove} operation
     *        on the collection.
     * @param swapState The state which enables or disables the {@code swap} operation on
     *        the collection.
     */
    public UnmodifiableIndexedCollection(IndexedCollection<E> collection,
            AddOperationState addState, RemoveOperationState removeState,
            SwapOperationState swapState) {
        this.collection = collection;
        this.addState = addState;
        this.removeState = removeState;
        this.swapState = swapState;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return collection.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#add(java.lang.Object)
     */
    @Override
    public boolean add(E e) {
        return addState.add(collection, e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#get(int)
     */
    @Override
    public E get(int index) {
        return collection.get(index);
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
        swapState.swap(collection, first, second);
    }
    
    /* 
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#set(int, java.lang.Object)
     */
    @Override
    public E set(int index, E e) {
        return collection.set(index, e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#
     * remove(int)
     */
    @Override
    public E remove(int index) {
        return removeState.remove(collection, index);
    }

    /* 
     * (non-Javadoc)
     *
     * @see 
     * fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#
     * remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return removeState.remove(collection, o);
    }

}
