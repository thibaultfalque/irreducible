/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.dynamic
 * package.
 *
 * It contains the EmptyCollectionManagementStrategy, which is the strategy used to manage
 * empty collections.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.dynamic;

import java.util.Collection;

/**
 * The EmptyCollectionManagementStrategy is the strategy used to manage empty collections.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class EmptyCollectionManagementStrategy implements CollectionManagementStrategy {

    /**
     * The managed collection.
     */
    private final Collection<?> managed;

    /**
     * Creates a new EmptyCollectionManagementStrategy.
     * 
     * @param managed The collection to manage.
     */
    public EmptyCollectionManagementStrategy(Collection<?> managed) {
        this.managed = managed;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.dynamic.CollectionManagementStrategy#
     * contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object element) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.dynamic.CollectionManagementStrategy#
     * add(java.lang.Object)
     */
    @Override
    public CollectionManagementStrategy add(Object element) {
        return new SmallCollectionManagementStrategy(managed);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.dynamic.CollectionManagementStrategy#
     * remove(java.lang.Object)
     */
    @Override
    public CollectionManagementStrategy remove(Object element) {
        throw new AssertionError("Cannot remove an object from an empty collection.");
    }

}
