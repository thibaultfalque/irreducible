/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.maps package.
 *
 * It contains the ArrayBasedMap, which is a map based on an array of fixed size.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.maps;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/**
 * The ArrayBasedMap is a map based on an array of fixed size.
 * The computation of the index is delegated to an external function.
 * 
 * @param <K> The type of the keys in the map.
 * @param <V> The type of the values in the map.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class ArrayBasedMap<K, V> extends AbstractMap<K, V> implements Iterable<Map.Entry<K, V>> {

    /**
     * The helper used to manage the keys of this map.
     */
    private final ArrayBasedMapHelper<K> helper;
    
    /**
     * The set-view of the mappings of this map.
     * It is lazily instantiated.
     */
    private Set<K> cachedKeySet;
    
    /**
     * The set-view of the mappings of this map.
     * It is lazily instantiated.
     */
    private Set<Entry<K, V>> cachedEntrySet;

    /**
     * The values associated to the keys.
     */
    private final V[] containedValues;

    /**
     * Creates a new ArrayBasedMap.
     * 
     * @param keyClass The class of the keys.
     * @param keyToIndex The function used to compute the index associated to a key.
     * @param indexToKey The function used to retrieve the key at a given index.
     * @param capacity The capacity of the map.
     */
    @SuppressWarnings("unchecked")
    public ArrayBasedMap(Class<K> keyClass, ToIntFunction<K> keyToIndex, 
            IntFunction<K> indexToKey, int capacity) {
        this.helper = new ArrayBasedMapHelper<>(keyClass, keyToIndex, indexToKey, capacity);
        this.containedValues = (V[]) new Object[capacity];
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#size()
     */
    @Override
    public int size() {
        return helper.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return helper.containsKey(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
        int index = helper.indexOf(key);
        return (index >= 0) ? containedValues[index] : null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(K key, V value) {
        int index = helper.put(key);
        V oldValue = containedValues[index];
        containedValues[index] = value;
        return oldValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#remove(java.lang.Object)
     */
    @Override
    public V remove(Object key) {
        int index = helper.remove(key);
        V removed = null;
        if (index >= 0) {
            removed = containedValues[index];
            containedValues[index] = null;
        }
        return removed;
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#clear()
     */
    @Override
    public void clear() {
        helper.clear();
        Arrays.fill(containedValues, null);
    }
    
    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#keySet()
     */
    @Override
    public Set<K> keySet() {
        if (cachedKeySet == null) {
            cachedKeySet = new ArrayBasedMapKeySet();
        }
        
        return cachedKeySet;
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#entrySet()
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        if (cachedEntrySet == null) {
            cachedEntrySet = new ArrayBasedMapEntrySet();
        }
        return cachedEntrySet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new ArrayBasedMapIterator();
    }
    
    /**
     * The ArrayBasedMapKeySet class provides a set view of the keys of this map.
     */
    private class ArrayBasedMapKeySet extends AbstractSet<K> {
        
        /**
         * The set view of the keys provided by the helper.
         */
        private Set<K> keySetHelper = helper.keySet();
        
        /* 
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return keySetHelper.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#contains(java.lang.Object)
         */
        @Override
        public boolean contains(Object object) {
            return keySetHelper.contains(object);
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#remove(java.lang.Object)
         */
        @Override
        public boolean remove(Object object) {
            int index = helper.indexOf(object);
            if (helper.isUsed(index)) {
                helper.remove(index);
                containedValues[index] = null;
                return true;
            }
            return false;
        }
        
        /* 
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#clear()
         */
        @Override
        public void clear() {
            ArrayBasedMap.this.clear();
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<K> iterator() {
            return keySetHelper.iterator();
        }
        
    }

    /**
     * The ArrayBasedMapEntrySet class provides a set view of the mappings of this
     * map.
     */
    private class ArrayBasedMapEntrySet extends AbstractSet<Entry<K, V>> {
        
        /* 
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return helper.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#contains(java.lang.Object)
         */
        @Override
        public boolean contains(Object object) {
            if (object instanceof Entry) {
                Entry<?, ?> entry = (Entry<?, ?>) object;
                V value = get(entry.getKey());
                if (Objects.equals(entry.getValue(), value)) {
                    return true;
                }
            }

            return false;
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#remove(java.lang.Object)
         */
        @Override
        public boolean remove(Object object) {
            if (object instanceof Entry) {
                Entry<?, ?> e = (Entry<?, ?>) object;
                return ArrayBasedMap.this.remove(e.getKey(), e.getValue());
            }

            return false;
        }
        
        /* 
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#clear()
         */
        @Override
        public void clear() {
            ArrayBasedMap.this.clear();
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return ArrayBasedMap.this.iterator();
        }
        
    }

    /**
     * The ArrayBasedMapIterator allows to iterate over the mappings of this map.
     */
    private class ArrayBasedMapIterator implements Iterator<Entry<K, V>> {

        /**
         * The iterator helper used to help to iterate over the mappings.
         */
        private ArrayBasedMapHelper<K>.IteratorHelper iteratorHelper = helper.iteratorHelper();

        /* 
         * (non-Javadoc)
         *
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return iteratorHelper.hasNext();
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.Iterator#next()
         */
        @Override
        public Entry<K, V> next() {
            if (hasNext()) {
                return new ArrayBasedMapEntry(iteratorHelper.next());
            }

            throw new NoSuchElementException();
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            int index = iteratorHelper.remove();
            containedValues[index] = null;
        }

    }

    /**
     * The ArrayBasedMapEntry represents a mapping in this map.
     */
    private class ArrayBasedMapEntry implements Entry<K, V> {

        /**
         * The helper for this entry.
         */
        private ArrayBasedMapHelper<K>.EntryHelper entry;

        /**
         * Creates a new ArrayBasedMapEntry.
         * 
         * @param entry The helper for the entry.
         */
        public ArrayBasedMapEntry(ArrayBasedMapHelper<K>.EntryHelper entry) {
            this.entry = entry;
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.Map.Entry#getKey()
         */
        @Override
        public K getKey() {
            return entry.getKey();
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.Map.Entry#getValue()
         */
        @Override
        public V getValue() {
            return containedValues[entry.getIndex()];
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.Map.Entry#setValue(java.lang.Object)
         */
        @Override
        public V setValue(V value) {
            V oldValue = containedValues[entry.getIndex()];
            containedValues[entry.getIndex()] = value;
            return oldValue;
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
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

            if (object instanceof Entry) {
                Entry<?, ?> e = (Entry<?, ?>) object;
                return Objects.equals(getKey(), e.getKey())
                        && Objects.equals(getValue(), e.getValue());
            }

            return false;
        }
        
    }

}
