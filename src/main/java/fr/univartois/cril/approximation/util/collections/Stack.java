/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections package.
 *
 * It contains the Stack, which defines the interface of a Last-In First-Out data
 * structure.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * The Stack defines the interface of a Last-In First-Out data structure.
 * 
 * @param <E> The type of the elements in the stack.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public interface Stack<E> extends Iterable<E> {

    /**
     * Adds an element on the top of this stack.
     * 
     * @param e The element to add.
     */
    void push(E e);

    /**
     * Retrieves, but does not remove, the element on the top of this stack.
     * 
     * @return The element on the top of this stack.
     * 
     * @throws NoSuchElementException If this stack is empty.
     */
    E peek();

    /**
     * Retrieves and removes the element on the top of this stack.
     * 
     * @return The element on the top of this stack.
     * 
     * @throws NoSuchElementException If this stack is empty.
     */
    E pop();
    
    /**
     * Clears the content of this stack.
     */
    void clear();

    /**
     * Checks whether this stack is empty.
     * 
     * @return If this stack is empty.
     */
    boolean isEmpty();

    /**
     * Gives the size of this stack.
     * 
     * @return The size of this stack.
     */
    int size();

    /**
     * Gives the stream of the elements in this stack.
     * 
     * @return The stream of the elements in this stack.
     */
    Stream<E> stream();
    
}
