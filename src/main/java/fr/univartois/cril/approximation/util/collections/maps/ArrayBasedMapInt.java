/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.maps package.
 *
 * It contains the ArrayBasedMapInt, which is a map of integers based on an array of fixed
 * size.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.maps;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

import fr.univartois.cril.approximation.util.collections.MapInt;

/**
 * The ArrayBasedMapInt is a map of integers based on an array of fixed size.
 * The computation of the index is delegated to an external function.
 * 
 * @param <K> The type of the keys in the map.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class ArrayBasedMapInt<K> implements MapInt<K> {

    /**
     * The helper used to manage the keys of this map.
     */
    private final ArrayBasedMapHelper<K> helper;

    /**
     * The set-view of the keys of this map.
     * It is lazily instantiated.
     */
    private Set<K> keySet;

    /**
     * The set-view of the mappings of this map.
     * It is lazily instantiated.
     */
    private Set<Entry<K>> entrySet;

    /**
     * The values associated to the keys.
     */
    private final int[] values;

    /**
     * Creates a new ArrayBasedMapInt.
     * 
     * @param keyClass The class of the keys.
     * @param keyToIndex The function used to compute the index associated to a key.
     * @param indexToKey The function used to retrieve the key at a given index.
     * @param capacity The capacity of the map.
     */
    public ArrayBasedMapInt(Class<K> keyClass, ToIntFunction<K> keyToIndex,
            IntFunction<K> indexToKey, int capacity) {
        this.helper = new ArrayBasedMapHelper<>(keyClass, keyToIndex, indexToKey, capacity);
        this.values = new int[capacity];
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#size()
     */
    @Override
    public int size() {
        return helper.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.MapInt#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return helper.containsKey(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#containsValue(int)
     */
    @Override
    public boolean containsValue(int value) {
        for (int i = 0; i < values.length; i++) {
            if (helper.isUsed(i) && (values[i] == value)) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#get(java.lang.Object)
     */
    @Override
    public int get(Object key) {
        int index = helper.indexOf(key);
        return (index >= 0) ? values[index] : 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#safeGet(java.lang.Object)
     */
    @Override
    public OptionalInt safeGet(Object key) {
        int index = helper.indexOf(key);
        if ((index >= 0) && helper.isUsed(index)) {
            return OptionalInt.of(values[index]);
        }
        return OptionalInt.empty();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#put(java.lang.Object, int)
     */
    @Override
    public OptionalInt put(K key, int value) {
        int index = helper.indexOf(key);
        OptionalInt oldValue = OptionalInt.empty();
        
        if (helper.isUsed(index)) {
            oldValue = OptionalInt.of(values[index]);
        }
        
        helper.put(key);
        values[index] = value;
        return oldValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#putAll(fr.univartois.cril.
     * orpheus.utils.collections.MapInt)
     */
    @Override
    public void putAll(MapInt<? extends K> map) {
        for (Entry<? extends K> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#remove(java.lang.Object)
     */
    @Override
    public OptionalInt remove(Object key) {
        int index = helper.remove(key);
        return (index >= 0) ? OptionalInt.of(values[index]) : OptionalInt.empty();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#clear()
     */
    @Override
    public void clear() {
        helper.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#keySet()
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = helper.keySet();
        }
        return keySet;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#entrySet()
     */
    @Override
    public Set<Entry<K>> entrySet() {
        if (entrySet == null) {
            entrySet = new ArrayBasedMapIntEntrySet();
        }
        return entrySet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Entry<K>> iterator() {
        return new ArrayBasedMapIntIterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (Entry<K> entry : this) {
            hash += entry.hashCode();
        }
        return hash;
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

        if (object instanceof MapInt) {
            return entrySet().equals(((MapInt<?>) object).entrySet());
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Iterator<Entry<K>> iterator = iterator();
        if (!iterator.hasNext()) {
            return "{}";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (;;) {
            Entry<K> entry = iterator.next();
            K key = entry.getKey();
            builder.append((key == this) ? "(this Map)" : key);
            builder.append('=');
            builder.append(entry.getValue());
            if (!iterator.hasNext()) {
                return builder.append('}').toString();
            }
            builder.append(", ");
        }
    }

    /**
     * The ArrayBasedMapIntEntrySet class provides a set view of the mappings of this
     * map.
     */
    private class ArrayBasedMapIntEntrySet extends AbstractSet<Entry<K>> {

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
                Entry<?> entry = (Entry<?>) object;
                OptionalInt value = safeGet(entry.getKey());
                if (value.isPresent() && (entry.getValue() == value.getAsInt())) {
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
                Entry<?> entry = (Entry<?>) object;
                return ArrayBasedMapInt.this.remove(entry.getKey()) != null;
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
            ArrayBasedMapInt.this.clear();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<Entry<K>> iterator() {
            return ArrayBasedMapInt.this.iterator();
        }

    }

    /**
     * The ArrayBasedMapIntIterator allows to iterate over the mappings of this map.
     */
    private class ArrayBasedMapIntIterator implements Iterator<Entry<K>> {

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
        public Entry<K> next() {
            if (hasNext()) {
                return new ArrayBasedMapIntEntry(iteratorHelper.next());
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
            iteratorHelper.remove();
        }

    }

    /**
     * The ArrayBasedMapIntEntry represents a mapping in this map.
     */
    private class ArrayBasedMapIntEntry implements Entry<K> {

        /**
         * The helper for this entry.
         */
        private ArrayBasedMapHelper<K>.EntryHelper entry;

        /**
         * Creates a new ArrayBasedMapIntEntry.
         * 
         * @param entry The helper for the entry.
         */
        public ArrayBasedMapIntEntry(ArrayBasedMapHelper<K>.EntryHelper entry) {
            this.entry = entry;
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapInt.Entry#getKey()
         */
        @Override
        public K getKey() {
            return entry.getKey();
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapInt.Entry#getValue()
         */
        @Override
        public int getValue() {
            return values[entry.getIndex()];
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapInt.Entry#setValue(int)
         */
        @Override
        public int setValue(int value) {
            int oldValue = values[entry.getIndex()];
            values[entry.getIndex()] = value;
            return oldValue;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(getKey()) ^ Integer.hashCode(getValue());
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
                Entry<?> e = (Entry<?>) object;
                return Objects.equals(getKey(), e.getKey()) && (getValue() == e.getValue());
            }

            return false;
        }
    
    }
    
}
