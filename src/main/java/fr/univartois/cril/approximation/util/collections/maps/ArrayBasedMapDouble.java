/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.maps package.
 *
 * It contains the ArrayBasedMapDouble, which is a map of doubles based on an array of
 * fixed size.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.maps;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

import fr.univartois.cril.approximation.util.collections.MapDouble;

/**
 * The ArrayBasedMapDouble is a map of doubles based on an array of fixed size.
 * The computation of the index is delegated to an external function.
 * 
 * @param <K> The type of the keys in the map.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class ArrayBasedMapDouble<K> implements MapDouble<K> {

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
    private final double[] values;

    /**
     * Creates a new ArrayBasedMapDouble.
     * 
     * @param keyClass The class of the keys.
     * @param keyToIndex The function used to compute the index associated to a key.
     * @param indexToKey The function used to retrieve the key at a given index.
     * @param capacity The capacity of the map.
     */
    public ArrayBasedMapDouble(Class<K> keyClass, ToIntFunction<K> keyToIndex,
            IntFunction<K> indexToKey, int capacity) {
        this.helper = new ArrayBasedMapHelper<>(keyClass, keyToIndex, indexToKey, capacity);
        this.values = new double[capacity];
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#size()
     */
    @Override
    public int size() {
        return helper.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#containsKey(java.lang.
     * Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return helper.containsKey(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#containsValue(double)
     */
    @Override
    public boolean containsValue(double value) {
        for (int i = 0; i < values.length; i++) {
            if (helper.isUsed(i) && (Double.compare(values[i], value) == 0)) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#get(java.lang.Object)
     */
    @Override
    public double get(Object key) {
        int index = helper.indexOf(key);
        return (index >= 0) ? values[index] : 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.MapDouble#safeGet(java.lang.Object)
     */
    @Override
    public OptionalDouble safeGet(Object key) {
        int index = helper.indexOf(key);
        if ((index >= 0) && helper.isUsed(index)) {
            return OptionalDouble.of(values[index]);
        }
        return OptionalDouble.empty();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#put(java.lang.Object,
     * double)
     */
    @Override
    public OptionalDouble put(K key, double value) {
        int index = helper.indexOf(key);
        OptionalDouble oldValue = OptionalDouble.empty();
        
        if (helper.isUsed(index)) {
            oldValue = OptionalDouble.of(values[index]);
        }
        
        helper.put(key);
        values[index] = value;
        return oldValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.MapDouble#putAll(fr.univartois.cril.
     * orpheus.utils.collections.MapDouble)
     */
    @Override
    public void putAll(MapDouble<? extends K> map) {
        for (Entry<? extends K> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.MapDouble#remove(java.lang.Object)
     */
    @Override
    public OptionalDouble remove(Object key) {
        int index = helper.remove(key);
        return (index >= 0) ? OptionalDouble.of(values[index]) : OptionalDouble.empty();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#clear()
     */
    @Override
    public void clear() {
        helper.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#keySet()
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
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#entrySet()
     */
    @Override
    public Set<Entry<K>> entrySet() {
        if (entrySet == null) {
            entrySet = new ArrayBasedMapDoubleEntrySet();
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
        return new ArrayBasedMapDoubleIterator();
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

        if (object instanceof MapDouble) {
            return entrySet().equals(((MapDouble<?>) object).entrySet());
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
     * The ArrayBasedMapDoubleEntrySet class provides a set view of the mappings of this
     * map.
     */
    private class ArrayBasedMapDoubleEntrySet extends AbstractSet<Entry<K>> {

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
                OptionalDouble value = safeGet(entry.getKey());
                if (value.isPresent() && (Double.compare(entry.getValue(), value.getAsDouble()) == 0)) {
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
                return ArrayBasedMapDouble.this.remove(entry.getKey()) != null;
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
            ArrayBasedMapDouble.this.clear();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<Entry<K>> iterator() {
            return ArrayBasedMapDouble.this.iterator();
        }

    }

    /**
     * The ArrayBasedMapDoubleIterator allows to iterate over the mappings of this map.
     */
    private class ArrayBasedMapDoubleIterator implements Iterator<Entry<K>> {

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
                return new ArrayBasedMapDoubleEntry(iteratorHelper.next());
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
     * The ArrayBasedMapDoubleEntry represents a mapping in this map.
     */
    private class ArrayBasedMapDoubleEntry implements Entry<K> {

        /**
         * The helper for this entry.
         */
        private ArrayBasedMapHelper<K>.EntryHelper entry;

        /**
         * Creates a new ArrayBasedMapDoubleEntry.
         * 
         * @param entry The helper for the entry.
         */
        public ArrayBasedMapDoubleEntry(ArrayBasedMapHelper<K>.EntryHelper entry) {
            this.entry = entry;
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapDouble.Entry#getKey()
         */
        @Override
        public K getKey() {
            return entry.getKey();
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapDouble.Entry#getValue()
         */
        @Override
        public double getValue() {
            return values[entry.getIndex()];
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * fr.univartois.cril.orpheus.utils.collections.MapDouble.Entry#setValue(double)
         */
        @Override
        public double setValue(double value) {
            double oldValue = values[entry.getIndex()];
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
            return Objects.hashCode(getKey()) ^ Double.hashCode(getValue());
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
