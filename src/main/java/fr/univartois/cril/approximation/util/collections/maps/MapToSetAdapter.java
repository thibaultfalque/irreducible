/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.maps package.
 *
 * It contains the MapToSetAdapter, which is an implementation of Set which adapts a Map.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.maps;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The MapToSetAdapter is an implementation of {@link Set} which adapts a {@link Map}.
 * 
 * @param <E> The type of the elements in the set.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class MapToSetAdapter<E> implements Set<E> {

    /**
     * The adapted map.
     */
    private final Map<E, E> adaptee;
    
    /**
     * The set view of the keys contained in this map
     */
    private final Set<E> keySet;

    /**
     * Creates a new MapToSetAdapter.
     * 
     * @param adaptee The map to adapt.
     * 
     * @throws NullPointerException If {@code adaptee} is {@code null}.
     */
    public MapToSetAdapter(Map<E, E> adaptee) {
        this.adaptee = Objects.requireNonNull(adaptee);
        this.keySet = adaptee.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#size()
     */
    @Override
    public int size() {
        return adaptee.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return adaptee.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#add(java.lang.Object)
     */
    @Override
    public boolean add(E e) {
        return adaptee.put(Objects.requireNonNull(e), e) == null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean any = false;
        for (E e : collection) {
            any |= add(e);
        }
        return any;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return adaptee.containsKey(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return adaptee.remove(o, o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return removeIf(c::contains);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return removeIf(e -> !c.contains(e));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#clear()
     */
    @Override
    public void clear() {
        adaptee.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#toArray()
     */
    @Override
    public Object[] toArray() {
        return keySet.toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return keySet.toArray(a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return keySet.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return keySet.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return keySet.equals(obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return keySet.toString();
    }

}
