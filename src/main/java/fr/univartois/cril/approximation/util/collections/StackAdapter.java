/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections package.
 *
 * It contains the StackAdapter, which adapts a Deque to a Stack.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections;

import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The StackAdapter adapts a {@link Deque} to a {@link Stack}.
 * 
 * @param <E> The type of the elements in the stack.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class StackAdapter<E> implements Stack<E> {

    /**
     * The adapted deque.
     */
    private final Deque<E> adaptee;

    /**
     * Creates a new StackAdapter.
     * 
     * @param deque The deque to adapt.
     * 
     * @throws NullPointerException If {@code deque} is {@code null}.
     */
    public StackAdapter(Deque<E> deque) {
        this.adaptee = Objects.requireNonNull(deque);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.Stack#push(java.lang.Object)
     */
    @Override
    public void push(E e) {
        adaptee.addFirst(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.Stack#peek()
     */
    @Override
    public E peek() {
        return adaptee.getFirst();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.Stack#pop()
     */
    @Override
    public E pop() {
        return adaptee.removeFirst();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.Stack#clear()
     */
    @Override
    public void clear() {
        adaptee.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.Stack#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return adaptee.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.Stack#size()
     */
    @Override
    public int size() {
        return adaptee.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return adaptee.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.univartois.cril.orpheus.utils.collections.Stack#stream()
     */
    @Override
    public Stream<E> stream() {
        return adaptee.stream();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return adaptee.hashCode();
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

        if (!(object instanceof Stack)) {
            return false;
        }

        // Comparing the sizes of the stacks.
        Stack<?> other = (Stack<?>) object;
        if (size() != other.size()) {
            return false;
        }

        // Comparing each element one by one.
        for (Iterator<?> it = iterator(), otherIt = other.iterator(); it.hasNext() && otherIt.hasNext();) {
            if (!Objects.equals(it.next(), otherIt.next())) {
                return false;
            }
        }

        // Both stacks contain the same elements in the same order.
        return true;
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

}
