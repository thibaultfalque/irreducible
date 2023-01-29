/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.indexed
 * package.
 *
 * It contains the ListIndexedCollection, which adapts a List to an IndexedCollection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.indexed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;

/**
 * The ListIndexedCollection adapts a {@link List} to an {@link IndexedCollection}.
 *
 * @param <E> The type of the element in the collection.
 * 
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class ListIndexedCollection<E> extends AbstractIndexedCollection<E> {

    /**
     * The adapted list.
     */
    private final List<E> adaptee;

    /**
     * Creates a new ListIndexedCollection.
     */
    public ListIndexedCollection() {
        this(new ArrayList<>());
    }

    /**
     * Creates a new ListIndexedCollection with the given initial size.
     * 
     * @param initialSize The initial size of the collection.
     */
    public ListIndexedCollection(int initialSize) {
        this(new ArrayList<>(initialSize));
    }

    /**
     * Creates a new ListIndexedCollection initialized with the elements of the given
     * collection.
     * 
     * @param collection The collection to initialize the ListIndexedCollection with.
     * 
     * @throws NullPointerException If {@code collection} is {@code null}.
     */
    public ListIndexedCollection(Collection<? extends E> collection) {
        this(collection.size());
        addAll(collection);
    }

    /**
     * Creates a new ListIndexedCollection which adapts the given list.
     * 
     * @param adaptee The List to adapt.
     * 
     * @throws NullPointerException If {@code adaptee} is {@code null}.
     */
    public ListIndexedCollection(List<E> adaptee) {
        this.adaptee = Objects.requireNonNull(adaptee);
    }

    /**
     * Gives a view of this collection as a {@link List}.
     * 
     * @return A view of this collection as a {@link List}.
     */
    public List<E> asList() {
        return adaptee;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return adaptee.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#add(java.lang.Object)
     */
    @Override
    public boolean add(E element) {
        return adaptee.add(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#get(int)
     */
    @Override
    public E get(int index) {
        return adaptee.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#swap
     * (int, int)
     */
    @Override
    public void swap(int first, int second) {
        Collections.swap(adaptee, first, second);
    }

    /* 
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#set(int,
     * java.lang.Object)
     */
    @Override
    public E set(int index, E e) {
        return adaptee.set(index, e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.univartois.cril.orpheus.utils.collections.indexed.AbstractIndexedCollection#
     * remove(int)
     */
    @Override
    public E remove(int index) {
        swap(index, size() - 1);
        return adaptee.remove(size() - 1);
    }
    
    /* (non-Javadoc)
     * @see java.util.AbstractCollection#clear()
     */
    @Override
    public void clear() {
        adaptee.clear();
    }

}
