/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.dynamic
 * package.
 *
 * It contains the SmallCollectionManagementStrategy, which is the strategy used to manage
 * small collections.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.dynamic;

import java.util.Collection;
import java.util.Objects;

/**
 * The SmallCollectionManagementStrategy is the strategy used to manage small collections.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class SmallCollectionManagementStrategy implements CollectionManagementStrategy {

    /**
     * The collection managed by this strategy.
     */
    private final Collection<?> managed;

    /**
     * Creates a new SmallCollectionManagementStrategy.
     * 
     * @param managed The collection to manage.
     */
    public SmallCollectionManagementStrategy(Collection<?> managed) {
        this.managed = managed;
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
        if (managed.size() >= LargeCollectionManagementStrategy.THRESHOLD) {
            // The collection has become large.
            return new LargeCollectionManagementStrategy(managed);
        }

        return this;
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
        if (managed.isEmpty()) {
            // The collection is now empty.
            return new EmptyCollectionManagementStrategy(managed);
        }

        return this;
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
        return managed.stream().anyMatch(e -> Objects.equals(e, element));
    }

}
