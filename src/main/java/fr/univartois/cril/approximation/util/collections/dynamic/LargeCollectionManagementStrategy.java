/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.dynamic
 * package.
 *
 * It contains the LargeCollectionManagementStrategy, which is the strategy used to manage
 * large collections.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.dynamic;

import java.util.Collection;
import java.util.Objects;

import fr.univartois.cril.approximation.util.collections.BloomFilterable;

/**
 * The LargeCollectionManagementStrategy is the strategy used to manage large
 * collections.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class LargeCollectionManagementStrategy implements CollectionManagementStrategy {

    /**
     * The threshold defining when a collection becomes large.
     */
    public static final int THRESHOLD = 10;

    /**
     * The collection managed by this strategy.
     */
    private final Collection<?> managed;

    /**
     * The Bloom filter used to efficiently check whether an element is not in the
     * collection.
     */
    private final byte[] bloomFilter = new byte[128];

    /**
     * Creates a new LargeCollectionManagementStrategy.
     * 
     * @param managed The collection to manage.
     */
    public LargeCollectionManagementStrategy(Collection<?> managed) {
        this.managed = managed;
        managed.forEach(this::add);
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
        return possiblyContains(element) && managed.stream().anyMatch(e -> Objects.equals(e, element));
    }

    /**
     * Checks whether the given element is possibly contained in the managed
     * collection, by using the Bloom filter.
     * 
     * @param element The element to check.
     * 
     * @return If the element is possibly in the collection.
     *         When this method returns {@code true}, the element may be in the
     *         collection, but it also may not.
     *         When this method returns {@code false}, the element cannot be in the
     *         collection.
     */
    private boolean possiblyContains(Object element) {
        for (int hashCode : asBloomFilterable(element).hashCodes(bloomFilter.length)) {
            if (bloomFilter[hashCode] == 0) {
                return false;
            }
        }

        return true;
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
        for (int hashCode : asBloomFilterable(element).hashCodes(bloomFilter.length)) {
            bloomFilter[hashCode]++;
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
        for (int hashCode : asBloomFilterable(element).hashCodes(bloomFilter.length)) {
            bloomFilter[hashCode]--;
        }

        if (managed.size() < THRESHOLD) {
            // The collection has become small.
            return new SmallCollectionManagementStrategy(managed);
        }

        return this;
    }

    /**
     * Adapts, if needed, the given object to a {@link BloomFilterable}.
     * 
     * @param element The element to adapt.
     * 
     * @return An adapter for the given object, or the object itself if it implements
     *         {@link BloomFilterable}.
     */
    private static BloomFilterable asBloomFilterable(Object element) {
        if (element instanceof BloomFilterable) {
            return (BloomFilterable) element;
        }

        return () -> new int[] { Objects.hashCode(element) };
    }

}
