/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.maps package.
 *
 * It contains the ArrayBasedMapHelper, which manages the keys of an array-based map
 * without considering the values, so that this helper can be reused for any type, even
 * primitive ones.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.maps;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/**
 * The ArrayBasedMapHelper manages the keys of an array-based map without considering the
 * values, so that this helper can be reused for any type, even primitive ones.
 *
 * @param <K> The type of the keys in the map.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class ArrayBasedMapHelper<K> {

    /**
     * The class of the keys.
     */
    private final Class<K> keyClass;

    /**
     * The function used to compute the index associated to a key.
     */
    private final ToIntFunction<K> keyToIndex;

    /**
     * The function used to retrieve the key at a given index.
     */
    private final IntFunction<K> indexToKey;

    /**
     * The number of used keys.
     */
    private int nbKeys;

    /**
     * The array marking the keys currently in use.
     */
    private final boolean[] usedKeys;

    /**
     * The set-view of the keys of the map.
     * It is lazily instantiated.
     */
    private Set<K> keySet;

    /**
     * Creates a new ArrayBasedMapHelper.
     *
     * @param keyClass The class of the keys.
     * @param keyToIndex The function used to compute the index associated to a key.
     * @param indexToKey The function used to retrieve the key at a given index.
     * @param capacity The capacity of the map.
     */
    public ArrayBasedMapHelper(Class<K> keyClass, ToIntFunction<K> keyToIndex,
            IntFunction<K> indexToKey, int capacity) {
        this.keyClass = keyClass;
        this.keyToIndex = keyToIndex;
        this.indexToKey = indexToKey;
        this.usedKeys = new boolean[capacity];
    }

    /**
     * Returns the number of key-value mappings in the map.
     *
     * @return The number of key-value mappings in the map.
     */
    public int size() {
        return nbKeys;
    }

    /**
     * Puts a key into the map.
     * Putting the value is left to the map implementation.
     *
     * @param key The key to put into the map.
     *
     * @return The index at which the key has been put.
     */
    public int put(K key) {
        int index = indexOf(key);

        if (!usedKeys[index]) {
            usedKeys[index] = true;
            nbKeys++;
        }

        return index;
    }

    /**
     * Returns {@code true} if the map contains a mapping for the specified key.
     *
     * @param key The key whose presence in the map is to be tested.
     *
     * @return {@code true} if the map contains a mapping for the specified key.
     */
    public boolean containsKey(Object key) {
        if (keyClass.isInstance(key)) {
            return usedKeys[indexOf(key)];
        }

        return false;
    }

    /**
     * Checks whether the key at the given index is used.
     *
     * @param index The index to check.
     *
     * @return If the key with the given index is used.
     */
    public boolean isUsed(int index) {
        return usedKeys[index];
    }

    /**
     * Gives the index of a key in the map.
     *
     * @param key The key to give the index of.
     *
     * @return The index of the key.
     */
    @SuppressWarnings("unchecked")
    public int indexOf(Object key) {
        if (keyClass.isInstance(key)) {
            return keyToIndex.applyAsInt((K) key);
        }

        return -1;
    }

    /**
     * Gives the key at the given index in the map.
     *
     * @param index The index of the key to give.
     *
     * @return The key at the given index, or {@code null} if the key is not present
     *         in the map.
     *
     * @throws IndexOutOfBoundsException If the given index is out of the bounds of
     *         the underlying array.
     */
    public K keyOf(int index) {
        if (usedKeys[index]) {
            return indexToKey.apply(index);
        }
        return null;
    }

    /**
     * Removes the key at the given index in the map.
     * Removing the value is left to the map implementation.
     *
     * @param index The index of the key to remove.
     *
     * @return If the key to remove was used.
     *
     * @throws IndexOutOfBoundsException If the given index is out of the bounds of
     *         the underlying array.
     */
    public boolean remove(int index) {
        if (usedKeys[index]) {
            usedKeys[index] = false;
            nbKeys--;
            return true;
        }

        return false;
    }

    /**
     * Removes the given key from the map.
     * Removing the value is left to the map implementation.
     *
     * @param key The key to remove.
     *
     * @return The index of the removed key, or {@code -1} if the key was not used.
     */
    public int remove(Object key) {
        if (keyClass.isInstance(key)) {
            int index = indexOf(key);
            if ((index >= 0) && remove(index)) {
                return index;
            }
        }

        return -1;
    }

    /**
     * Removes all of the keys from the map.
     * Removing all the values is left to the map implementation.
     */
    public void clear() {
        Arrays.fill(usedKeys, false);
        nbKeys = 0;
    }

    /**
     * Gives a set view of the keys of the map.
     *
     * @return A set view of the keys of the map.
     */
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new ArrayBasedMapHelperKeySet();
        }

        return keySet;
    }

    /**
     * Gives an iterator helper to help to iterate over the map.
     *
     * @return The helper for the iteration.
     */
    public IteratorHelper iteratorHelper() {
        return new IteratorHelper();
    }

    /**
     * Gives the index of the next used key, starting from the given index (excluded).
     *
     * @param index The index to start from.
     *
     * @return The next index which is used, or {@code -1} if there is not.
     */
    public int nextUsedKey(int index) {
        for (int i = index + 1; i < usedKeys.length; i++) {
            if (usedKeys[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Creates an entry helper for the entry at the given index.
     *
     * @param index The index of the entry to create.
     *
     * @return An entry helper for the entry at the given index.
     */
    public EntryHelper entryHelper(int index) {
        return new EntryHelper(index);
    }

    /**
     * The ArrayBasedMapHelperKeySet class provides a set view of the keys of this map.
     */
    private class ArrayBasedMapHelperKeySet extends AbstractSet<K> {

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return ArrayBasedMapHelper.this.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#contains(java.lang.Object)
         */
        @Override
        public boolean contains(Object object) {
            return containsKey(object);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#remove(java.lang.Object)
         */
        @Override
        public boolean remove(Object o) {
            return ArrayBasedMapHelper.this.remove(o) >= 0;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#clear()
         */
        @Override
        public void clear() {
            ArrayBasedMapHelper.this.clear();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<K> iterator() {
            return new Iterator<>() {

                /**
                 * The iterator helper helping to iterate over the map.
                 */
                private IteratorHelper helper = iteratorHelper();

                /*
                 * (non-Javadoc)
                 *
                 * @see java.util.Iterator#hasNext()
                 */
                @Override
                public boolean hasNext() {
                    return helper.hasNext();
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see java.util.Iterator#hasNext()
                 */
                @Override
                public K next() {
                    return helper.next().getKey();
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see java.util.Iterator#hasNext()
                 */
                @Override
                public void remove() {
                    helper.remove();
                }

            };
        }

    }

    /**
     * The IteratorHelper allows to iterate over the mappings of this map.
     */
    public class IteratorHelper {

        /**
         * The index of the current key.
         */
        private int index = nextUsedKey(-1);

        /**
         * The index of the previous key.
         */
        private int previousIndex = -1;

        /**
         * Checks whether there is a next key.
         *
         * @return If there is a next key.
         */
        public boolean hasNext() {
            return index >= 0;
        }

        /**
         * Gives the index of the next key.
         *
         * @return The index of the next key.
         *
         * @throws NoSuchElementException If there is no next element.
         */
        public EntryHelper next() {
            if (hasNext()) {
                previousIndex = index;
                index = nextUsedKey(index);
                return entryHelper(previousIndex);
            }

            throw new NoSuchElementException();
        }

        /**
         * Removes the last read key.
         *
         * @return The index of the removed key.
         */
        public int remove() {
            ArrayBasedMapHelper.this.remove(previousIndex);
            return previousIndex;
        }

    }

    /**
     * The EntryHelper represents a mapping in this map.
     */
    public class EntryHelper {

        /**
         * The index of the entry.
         */
        private int index;

        /**
         * Creates a new EntryHelper.
         *
         * @param index The index of the entry.
         */
        public EntryHelper(int index) {
            this.index = index;
        }

        /**
         * Gives the key of this entry.
         *
         * @return The key of this entry.
         */
        public K getKey() {
            return keyOf(index);
        }

        /**
         * Gives the index of this entry.
         *
         * @return The index of this entry.
         */
        public int getIndex() {
            return index;
        }

    }

}
