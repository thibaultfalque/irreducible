/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.maps package.
 *
 * It contains the MapDoubleAdapter, which adapts a Map to a MapDouble.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.maps;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Set;

import fr.univartois.cril.approximation.util.collections.MapDouble;

/**
 * The MapDoubleAdapter adapts a {@link Map} to a {@link MapDouble}.
 * 
 * @param <K> The type of the keys.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class MapDoubleAdapter<K> implements MapDouble<K> {

    /**
     * The adapted map.
     */
    private final Map<K, Double> adaptee;

    /**
     * The set view of the mappings of this map.
     */
    private Set<Entry<K>> entrySet;

    /**
     * Creates a new MapDoubleAdapter.
     * 
     * @param adaptee The map to adapt.
     */
    public MapDoubleAdapter(Map<K, Double> adaptee) {
        this.adaptee = adaptee;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#size()
     */
    @Override
    public int size() {
        return adaptee.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return adaptee.isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#containsKey(java.lang.
     * Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return adaptee.containsKey(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#containsValue(double)
     */
    @Override
    public boolean containsValue(double value) {
        return adaptee.containsValue(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#get(java.lang.Object)
     */
    @Override
    public double get(Object key) {
        Double value = adaptee.get(key);
        return (value == null) ? 0 : value;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.MapDouble#safeGet(java.lang.Object)
     */
    @Override
    public OptionalDouble safeGet(Object key) {
        Double value = adaptee.get(key);
        return (value == null) ? OptionalDouble.empty() : OptionalDouble.of(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#put(java.lang.Object,
     * double)
     */
    @Override
    public OptionalDouble put(K key, double value) {
        Double oldValue = adaptee.put(key, value);
        return (oldValue == null) ? OptionalDouble.empty() : OptionalDouble.of(oldValue);
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
        Double removed = adaptee.remove(key);
        return (removed == null) ? OptionalDouble.empty() : OptionalDouble.of(removed);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#clear()
     */
    @Override
    public void clear() {
        adaptee.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#keySet()
     */
    @Override
    public Set<K> keySet() {
        return adaptee.keySet();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapDouble#entrySet()
     */
    @Override
    public Set<Entry<K>> entrySet() {
        if (entrySet == null) {
            entrySet = new MapDoubleAdapterEntrySet();
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
        return new MapDoubleAdapterIterator();
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
        return adaptee.toString();
    }

    /**
     * The MapDoubleAdapterEntrySet class provides a set view of the mappings of this map.
     */
    private class MapDoubleAdapterEntrySet extends AbstractSet<Entry<K>> {

        /**
         * The entry-set of the adapted map.
         */
        private Set<Map.Entry<K, Double>> adaptedSet = adaptee.entrySet();

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return adaptedSet.size();
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
                return adaptedSet.remove(Map.entry(entry.getKey(), entry.getValue()));
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
            adaptedSet.clear();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<Entry<K>> iterator() {
            return MapDoubleAdapter.this.iterator();
        }
        
    }

    /**
     * The MapDoubleAdapterIterator allows to iterate over the mappings of this map.
     */
    private class MapDoubleAdapterIterator implements Iterator<Entry<K>> {

        /**
         * The iterator over the mappings of the adapted map.
         */
        private Iterator<Map.Entry<K, Double>> adaptedIterator = adaptee.entrySet().iterator();

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return adaptedIterator.hasNext();
        }

        /* 
         * (non-Javadoc)
         *
         * @see java.util.Iterator#next()
         */
        @Override
        public Entry<K> next() {
            return new MapDoubleAdapterEntry<>(adaptedIterator.next());
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            adaptedIterator.remove();
        }

    }

    /**
     * The MapDoubleAdapterEntry represents a mapping in this map.
     * 
     * @param <K> The type of the key.
     */
    private static class MapDoubleAdapterEntry<K> implements Entry<K> {

        /**
         * The entry of the adapted map.
         */
        private Map.Entry<K, Double> adaptedEntry;

        /**
         * Creates a new MapDoubleAdapterEntry.
         * 
         * @param adaptedEntry The entry to adapt.
         */
        public MapDoubleAdapterEntry(java.util.Map.Entry<K, Double> adaptedEntry) {
            this.adaptedEntry = adaptedEntry;
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapDouble.Entry#getKey()
         */
        @Override
        public K getKey() {
            return adaptedEntry.getKey();
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapDouble.Entry#getValue()
         */
        @Override
        public double getValue() {
            return adaptedEntry.getValue();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * fr.univartois.cril.orpheus.utils.collections.MapDouble.Entry#setValue(double)
         */
        @Override
        public double setValue(double value) {
            return adaptedEntry.setValue(value);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return adaptedEntry.hashCode();
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
                Entry<?> entry = (Entry<?>) object;
                return Objects.equals(getKey(), entry.getKey())
                        && (Double.compare(getValue(), entry.getValue()) == 0);
            }

            return false;
        }

    }

}
