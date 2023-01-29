/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.dynamic package.
 *
 * It contains the CollectionManagementStrategy, which enables to dynamically determine
 * the best strategy to use to manage a collection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.dynamic;

/**
 * The CollectionManagementStrategy enables to dynamically determine the best strategy to
 * use to manage a collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public interface CollectionManagementStrategy {

    /**
     * Notifies this strategy that an element has been added to the collection.
     * 
     * @param element The added element.
     * 
     * @return The management strategy to use after having added the given element.
     */
    CollectionManagementStrategy add(Object element);

    /**
     * Notifies this strategy that an element has been removed from the collection.
     * 
     * @param element The removed element.
     * 
     * @return The management strategy to use after having removed the given element.
     */
    CollectionManagementStrategy remove(Object element);

    /**
     * Checks whether the collection contains the given element.
     * 
     * @param element The element to check.
     * 
     * @return If the collection contains the element.
     */
    boolean contains(Object element);

}
