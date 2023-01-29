/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections package.
 *
 * It contains the IndexedCollection, a collection allowing to access its elements by
 * their index.
 *
 * (c) Romain WALLON - Orpheus
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections;

import java.util.Collection;
import java.util.List;

/**
 * The IndexedCollection is a collection allowing to access its elements by their index.
 * 
 * It differs from {@link List} by the fact that implementing classes does not necessarily
 * let the user have a total control on the order of the elements.
 * In particular, the insertion order is not necessarily respected.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public interface IndexedCollection<E> extends Collection<E> {

    /**
     * Gives the element at the given index.
     * 
     * @param index The index of the element to get.
     * 
     * @return The element at the given index.
     * 
     * @throws IndexOutOfBoundsException If the index is out of the bounds of this
     *         collection.
     */
    E get(int index);

    /**
     * Gives the index of the given object in this collection.
     * 
     * @param object The object to give the index of.
     * 
     * @return The index of the object, or {@code -1} if this collection does not contain
     *         the object.
     */
    int indexOf(Object object);

    /**
     * Swaps the elements at the given indices.
     * 
     * @param first The index of the first element.
     * @param second The index of the second element.
     * 
     * @throws IndexOutOfBoundsException If one of the indices is out of the bounds of
     *         this collection.
     * @throws UnsupportedOperationException If this collection does not support
     *         the {@code swap} operation.
     */
    void swap(int first, int second);

    /**
     * Sets the element at the given index.
     * 
     * @param index The index of the element to set.
     * @param e The new element.
     * 
     * @return The element which was previously at the given index.
     * 
     * @throws IndexOutOfBoundsException If the given index is out of the bounds of
     *         this collection.
     * @throws UnsupportedOperationException If this collection does not support
     *         the {@code set} operation.
     */
    E set(int index, E e);

    /**
     * Removes the element at the given index in this collection.
     * This method replaces the removed object by the last element of the collection.
     * 
     * @param index The index of the element to remove.
     * 
     * @return The element that has been removed.
     * 
     * @throws IndexOutOfBoundsException If the index is out of the bounds of this
     *         collection.
     * @throws UnsupportedOperationException If this collection does not support
     *         the {@code remove} operation.
     */
    E remove(int index);

    /**
     * Removes an object from this collection.
     * This method replaces the removed object by the last element of the collection.
     * 
     * @param object The object to remove.
     * 
     * @return Whether the element has been removed.
     * 
     * @throws UnsupportedOperationException If this collection does not support
     *         the {@code remove} operation.
     */
    @Override
    boolean remove(Object object);

    /**
     * Fastly clears the content of this collection.
     * After a call to this method, the collection must look empty.
     * However, this method is not required to actually remove all the contained elements.
     * 
     * So, use this method with caution, as it may lead to memory leaks.
     * 
     * @implSpec By default, this method simply invokes {@link #clear()}.
     * 
     * @see #clear()
     */
    default void fastClear() {
        clear();
    }

    /**
     * Tries to reduce the internal size of this collection to fit exactly the number
     * of its elements.
     * 
     * @implSpec The default implementation does nothing.
     */
    default void trimToSize() {
        // Nothing to do by default.
    }

    /**
     * Gives an unmodifiable view of the sub-collection starting from the index {@code 0}
     * (included) up to the index {@code end} (excluded).
     * 
     * @param end The upper index (excluded) of the sub-collection to get.
     * 
     * @return The sub-collection from the {@code 0}-th index to the {@code end}-th.
     * 
     * @throws IndexOutOfBoundsException If {@code end} is out of the bounds of the
     *         collection.
     * 
     * @implSpec The default implementation calls {@link #subCollection(int, int)}
     *           with {@code 0} and {@code end} as arguments.
     * 
     * @see #subCollection(int, int)
     */
    default IndexedCollection<E> subCollection(int end) {
        return subCollection(0, end);
    }

    /**
     * Gives an unmodifiable view of the sub-collection starting from the index
     * {@code start} (included) up to the index {@code end} (excluded).
     * 
     * @param start The lower index (included) of the sub-collection to get.
     * @param end The upper index (excluded) of the sub-collection to get.
     * 
     * @return The sub-collection from the {@code start}-th index to the {@code end}-th.
     * 
     * @throws IndexOutOfBoundsException If the given range is out of the bounds of
     *         the collection.
     */
    IndexedCollection<E> subCollection(int start, int end);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#iterator()
     */
    @Override
    IndexedCollectionIterator<E> iterator();

}
