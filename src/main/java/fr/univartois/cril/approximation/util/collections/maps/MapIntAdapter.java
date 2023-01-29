/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.maps package.
 *
 * It contains the MapIntAdapter, which adapts a Map to a MapInt.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.maps;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;

import fr.univartois.cril.approximation.util.collections.MapInt;

/**
 * The MapIntAdapter adapts a {@link Map} to a {@link MapInt}.
 * 
 * @param <K> The type of the keys.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class MapIntAdapter<K> implements MapInt<K> {

    /**
     * The adapted map.
     */
    private final Map<K, Integer> adaptee;

    /**
     * The set view of the mappings of this map.
     */
    private Set<Entry<K>> entrySet;

    /**
     * Creates a new MapIntAdapter.
     * 
     * @param adaptee The map to adapt.
     */
    public MapIntAdapter(Map<K, Integer> adaptee) {
        this.adaptee = adaptee;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#size()
     */
    @Override
    public int size() {
        return adaptee.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return adaptee.isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.MapInt#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return adaptee.containsKey(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#containsValue(int)
     */
    @Override
    public boolean containsValue(int value) {
        return adaptee.containsValue(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#get(java.lang.Object)
     */
    @Override
    public int get(Object key) {
        Integer value = adaptee.get(key);
        return (value == null) ? 0 : value;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#safeGet(java.lang.Object)
     */
    @Override
    public OptionalInt safeGet(Object key) {
        Integer value = adaptee.get(key);
        return (value == null) ? OptionalInt.empty() : OptionalInt.of(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#put(java.lang.Object, int)
     */
    @Override
    public OptionalInt put(K key, int value) {
        Integer oldValue = adaptee.put(key, value);
        return (oldValue == null) ? OptionalInt.empty() : OptionalInt.of(oldValue);
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
        Integer removed = adaptee.remove(key);
        return (removed == null) ? OptionalInt.empty() : OptionalInt.of(removed);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#clear()
     */
    @Override
    public void clear() {
        adaptee.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#keySet()
     */
    @Override
    public Set<K> keySet() {
        return adaptee.keySet();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.MapInt#entrySet()
     */
    @Override
    public Set<Entry<K>> entrySet() {
        if (entrySet == null) {
            entrySet = new MapIntAdapterEntrySet();
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
        return new MapIntAdapterIterator();
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
        return adaptee.toString();
    }

    /**
     * The MapIntAdapterEntrySet class provides a set view of the mappings of this map.
     */
    private class MapIntAdapterEntrySet extends AbstractSet<Entry<K>> {

        /**
         * The entry-set of the adapted map.
         */
        private Set<Map.Entry<K, Integer>> adaptedSet = adaptee.entrySet();

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
            return MapIntAdapter.this.iterator();
        }

    }

    /**
     * The MapIntAdapterIterator allows to iterate over the mappings of this map.
     */
    private class MapIntAdapterIterator implements Iterator<Entry<K>> {

        /**
         * The iterator over the mappings of the adapted map.
         */
        private Iterator<Map.Entry<K, Integer>> adaptedIterator = adaptee.entrySet().iterator();

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
            return new MapIntAdapterEntry<>(adaptedIterator.next());
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
     * The MapIntAdapterEntry represents a mapping in this map.
     * 
     * @param <K> The type of the key.
     */
    private static class MapIntAdapterEntry<K> implements Entry<K> {

        /**
         * The entry of the adapted map.
         */
        private Map.Entry<K, Integer> adaptedEntry;

        /**
         * Creates a new MapIntAdapterEntry.
         * 
         * @param adaptedEntry The entry to adapt.
         */
        public MapIntAdapterEntry(Map.Entry<K, Integer> adaptedEntry) {
            this.adaptedEntry = adaptedEntry;
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapInt.Entry#getKey()
         */
        @Override
        public K getKey() {
            return adaptedEntry.getKey();
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapInt.Entry#getValue()
         */
        @Override
        public int getValue() {
            return adaptedEntry.getValue();
        }

        /*
         * (non-Javadoc)
         *
         * @see fr.univartois.cril.orpheus.utils.collections.MapInt.Entry#setValue(int)
         */
        @Override
        public int setValue(int value) {
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
                return Objects.equals(getKey(), entry.getKey()) && (getValue() == entry.getValue());
            }

            return false;
        }

    }

}
