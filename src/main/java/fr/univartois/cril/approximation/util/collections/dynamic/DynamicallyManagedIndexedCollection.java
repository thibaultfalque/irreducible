/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.dynamic
 * package.
 *
 * It contains the DynamicallyManagedIndexedCollection, which is a decorator to
 * dynamically manage an IndexedCollection.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.dynamic;

import java.util.Collection;

import fr.univartois.cril.approximation.util.collections.IndexedCollection;
import fr.univartois.cril.approximation.util.collections.IndexedCollectionIterator;
import fr.univartois.cril.approximation.util.collections.indexed.IndexedCollectionIteratorImpl;
import fr.univartois.cril.approximation.util.collections.indexed.SubIndexedCollection;

/**
 * The DynamicallyManagedIndexedCollection is a decorator to dynamically manage an
 * {@link IndexedCollection}.
 * 
 * In particular, the implementation of {@link #contains(Object)} varies with the size
 * of the collection.
 * 
 * @param <E> The type of the elements in the collection.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class DynamicallyManagedIndexedCollection<E> implements IndexedCollection<E> {

    /**
     * The managed collection.
     */
    private final IndexedCollection<E> managedCollection;

    /**
     * The strategy currently applied to manage the collection.
     */
    private CollectionManagementStrategy managingStrategy;

    /**
     * Creates a new DynamicallyManagedIndexedCollection.
     * 
     * @param managed The collection to manage.
     * 
     * @throws NullPointerException If {@code managed} is {@code null}.
     * @throws IllegalArgumentException If {@code managed} is not empty.
     */
    public DynamicallyManagedIndexedCollection(IndexedCollection<E> managed) {
        if (!managed.isEmpty()) {
            throw new IllegalArgumentException(
                    "Collection must be initially empty to be dynamically managed");
        }

        this.managedCollection = managed;
        this.managingStrategy = new EmptyCollectionManagementStrategy(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#size()
     */
    @Override
    public int size() {
        return managedCollection.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return managedCollection.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#add(java.lang.Object)
     */
    @Override
    public boolean add(E e) {
        if (managedCollection.add(e)) {
            managingStrategy = managingStrategy.add(e);
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean result = false;
        for (E e : collection) {
            result |= add(e);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#get(int)
     */
    @Override
    public E get(int index) {
        return managedCollection.get(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.IndexedCollection#indexOf(java.lang.
     * Object)
     */
    @Override
    public int indexOf(Object object) {
        return managedCollection.indexOf(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object object) {
        return managingStrategy.contains(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> collection) {
        return collection.stream().allMatch(this::contains);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#swap(int, int)
     */
    @Override
    public void swap(int first, int second) {
        managedCollection.swap(first, second);
    }

    /* 
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#set(int,
     * java.lang.Object)
     */
    @Override
    public E set(int index, E e) {
        E previous = managedCollection.set(index, e);
        managingStrategy = managingStrategy.remove(previous).add(e);
        return previous;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.orpheus.utils.collections.IndexedCollection#remove(int)
     */
    @Override
    public E remove(int index) {
        E removed = managedCollection.remove(index);
        managingStrategy = managingStrategy.remove(removed);
        return removed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object object) {
        if (managedCollection.remove(object)) {
            managingStrategy = managingStrategy.remove(object);
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean result = false;
        for (Object o : collection) {
            result |= remove(o);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        return removeIf(e -> !collection.contains(e));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#clear()
     */
    @Override
    public void clear() {
        managedCollection.clear();
        managingStrategy = new EmptyCollectionManagementStrategy(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#clear()
     */
    @Override
    public void fastClear() {
        managedCollection.fastClear();
        managingStrategy = new EmptyCollectionManagementStrategy(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#toArray()
     */
    @Override
    public Object[] toArray() {
        return managedCollection.toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(T[] array) {
        return managedCollection.toArray(array);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.orpheus.utils.collections.IndexedCollection#subCollection(int,
     * int)
     */
    @Override
    public IndexedCollection<E> subCollection(int start, int end) {
        return new SubIndexedCollection<>(this, start, end);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#iterator()
     */
    @Override
    public IndexedCollectionIterator<E> iterator() {
        return new IndexedCollectionIteratorImpl<>(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return managedCollection.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return managedCollection.equals(obj);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return managedCollection.toString();
    }

    /**
     * Gives the class of the manager of this collection.
     * This method should only be used for test purposes.
     * 
     * @return The class of this collection's manager.
     */
    Class<? extends CollectionManagementStrategy> getManagerClass() {
        return managingStrategy.getClass();
    }

}
