/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the AbstractIndexedCollection, which provides a skeletal implementation of
 * the IndexedCollection interface, to minimize the effort required to implement this
 * interface.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Objects;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;
import fr.univartois.cril.approximation.util.collections.IndexedCollectionIterator;

/**
 * The AbstractIndexedCollection provides a skeletal implementation of the
 * {@link IndexedCollection} interface, to minimize the effort required to
 * implement this interface.
 * 
 * To implement an unmodifiable collection, only {@code size} and {@code get}
 * must be implemented.
 * 
 * To implement a modifiable collection, the methods {@code add}, {@code remove}
 * and {@code swap} must be overridden.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public abstract class AbstractIndexedCollection<E> extends AbstractCollection<E> implements IndexedCollection<E> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.univartois.cril.orpheus.utils.collections.IndexedCollection#indexOf(java.lang.
     * Object)
     */
    @Override
    public int indexOf(Object object) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(get(i), object)) {
                return i;
            }
        }

        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#swap(int, int)
     */
    @Override
    public void swap(int first, int second) {
        throw new UnsupportedOperationException();
    }
    
    /* 
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#set(int, java.lang.Object)
     */
    @Override
    public E set(int index, E e) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#remove(int)
     */
    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }
    
    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object object) {
        int index = indexOf(object);
        
        if (index >= 0) {
            // Removing the index where the object is.
            remove(index);
            return true;
        }
        
        // The object is not in the collection.
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.univartois.cril.orpheus.utils.collections.IndexedCollection#subCollection(int,
     * int)
     */
    @Override
    public IndexedCollection<E> subCollection(int start, int end) {
        return new SubIndexedCollection<>(this, start, end);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#iterator()
     */
    @Override
    public IndexedCollectionIterator<E> iterator() {
        return new IndexedCollectionIteratorImpl<>(this);
    }
    
    /* 
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int code = 1;
        for (E e : this) {
            code += Objects.hashCode(e);
        }
        return code;
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        
        if (!(object instanceof IndexedCollection)) {
            return false;
        }

        // Comparing the sizes of both collections.
        IndexedCollection<?> other = (IndexedCollection<?>) object;
        if (other.size() != size()) {
            return false;
        }
        
        // Comparing each element one by one.
        for (Iterator<?> it = iterator(), otherIt = other.iterator(); it.hasNext() && otherIt.hasNext();) {
            if (!Objects.equals(it.next(), otherIt.next())) {
                return false;
            }
        }

        // Both stacks contain the same elements.
        return true;
    }
    
    /* (non-Javadoc)
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#fastClear()
     */
    @Override
    public void fastClear() {
        clear();
    }
    
}
