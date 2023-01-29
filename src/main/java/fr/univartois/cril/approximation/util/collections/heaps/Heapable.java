/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.heaps package.
 *
 * It contains the Heapable interface, which allows to efficiently retrieve an object in a
 * heap represented with an array.
 * 
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.heaps;

/**
 * The Heapable interface allows to efficiently retrieve an object in a heap represented
 * with an array.
 * 
 * Such an object can be put in at most one heap.
 * If this is not possible, consider the use of an object adapter.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public interface Heapable {

    /**
     * Gives the index of this object in the heap.
     * 
     * @return The index of this object in the heap.
     */
    int getIndex();

    /**
     * Sets the index of this object in the heap.
     * 
     * @param index The index to set.
     */
    void setIndex(int index);

}
