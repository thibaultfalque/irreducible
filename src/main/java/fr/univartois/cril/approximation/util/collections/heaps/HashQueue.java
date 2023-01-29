/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections.heaps package.
 *
 * It contains the HashQueue, which defines a queue with constant-time access to
 * any of its elements.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections.heaps;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * The HashQueue defines a queue with constant-time access to any of its elements.
 * Note that an element can appear at most once in the queue.
 * 
 * The queue also allows to access to its internal representation (which is linked-list
 * based) so that any low-level operation can be performed efficiently from outside when
 * needed.
 * 
 * @param <E> The type of the elements in the hash queue.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
public final class HashQueue<E> extends AbstractCollection<E> implements Queue<E> {

    /**
     * The HashQueueElement represents an element of the HashQueue.
     * 
     * @param <E> The type of the elements in the hash queue.
     */
    public static class HashQueueElement<E> {

        /**
         * The owner of this element.
         */
        private final HashQueue<E> owner;

        /**
         * The value stored in this element.
         */
        private final E value;

        /**
         * The previous element in the queue.
         */
        private HashQueueElement<E> previous;

        /**
         * The next element in the queue.
         */
        private HashQueueElement<E> next;

        /**
         * Creates a new HashQueueElement.
         * 
         * @param owner The owner of this element.
         * @param value The value stored in this element.
         */
        private HashQueueElement(HashQueue<E> owner, E value) {
            this.owner = owner;
            this.value = value;
        }

        /**
         * Gives the value stored in this element.
         * 
         * @return The value stored in this element.
         */
        public E getValue() {
            return value;
        }

        /**
         * Gives the element which follows this one in the queue.
         * 
         * @return The next element in the queue.
         */
        public HashQueueElement<E> getNext() {
            return next;
        }
        
        /**
         * Gives the element which precedes this one in the queue.
         * 
         * @return The previous element in the queue.
         */
        public HashQueueElement<E> getPrevious() {
            return previous;
        }
        
    }

    /**
     * The map allowing to access to the internal representation of an element in constant time.
     */
    private final Map<E, HashQueueElement<E>> cache;

    /**
     * The head of this queue.
     */
    private HashQueueElement<E> head;

    /**
     * The tail of this queue.
     */
    private HashQueueElement<E> tail;

    /**
     * Creates a new HashQueue.
     * 
     * @param cache The supplier for the cache to use to retrieve elements in constant time.
     */
    public HashQueue(Supplier<Map<E, HashQueueElement<E>>> cache) {
        this.cache = cache.get();
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return cache.size();
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object object) {
        return cache.containsKey(object);
    }

    /**
     * Gives the internal representation of the given element.
     * 
     * @param element The element to get the internal representation of.
     * 
     * @return The internal representation of {@code element}, or {@code null} if it is not 
     *         in this queue.
     */
    public HashQueueElement<E> get(Object element) {
        return cache.get(element);
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object object) {
        return rawRemove(cache.get(object));
    }

    /**
     * Removes an element given by its internal representation from this queue.
     * 
     * @param internalElement The internal representation of the element to remove.
     * 
     * @return If the element was successfully removed.
     * 
     * @throws IllegalArgumentException If {@code internalElement} is not owned by this
     *         queue.
     */
    public boolean rawRemove(HashQueueElement<E> internalElement) {
        // Checking the argument.
        ensureOwned(internalElement);
        if (internalElement == null) {
            return false;
        }

        // Updating the links.
        if (internalElement.next != null) {
            internalElement.next.previous = internalElement.previous;
        }
        if (internalElement.previous != null) {
            internalElement.previous.next = internalElement.next;
        }

        // Updating the head and tail.
        if (internalElement == head) {
            head = internalElement.next;
        }
        if (internalElement == tail) {
            tail = internalElement.previous;
        }

        // Removing the element from the cache.
        cache.remove(internalElement.value);

        // Resetting the element.
        internalElement.next = null;
        internalElement.previous = null;
        return true;
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#clear()
     */
    @Override
    public void clear() {
        head = null;
        tail = null;
        cache.clear();
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#add(java.lang.Object)
     */
    @Override
    public boolean add(E e) {
        return rawAdd(new HashQueueElement<>(this, e));
    }

    /**
     * Inserts the internal representation of an element into this queue.
     * 
     * @param internalElement The internal representation of the element to add.
     * 
     * @return Always {@code true}.
     * 
     * @throws NullPointerException If {@code internalElement} is {@code null}.
     * @throws IllegalArgumentException If {@code internalElement} is not owned by this
     *         queue.
     */
    public boolean rawAdd(HashQueueElement<E> internalElement) {
        Objects.requireNonNull(internalElement);
        ensureOwned(internalElement);
        
        if (head == null) {
            // This is the first element to be inserted.
            head = internalElement;

        } else {
            // Adding the element at the end.
            internalElement.previous = tail;
            tail.next = internalElement;
        }

        // Caching the element for a constant-time access.
        cache.put(internalElement.value, internalElement);

        // Updating the tail to be the new element.
        tail = internalElement;
        internalElement.next = null;
        return true;
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.util.Queue#offer(java.lang.Object)
     */
    @Override
    public boolean offer(E e) {
        return add(e);
    }

    /**
     * Inserts the internal representation of an element into this queue.
     * 
     * @param internalElement The internal representation of the element to add.
     * 
     * @return Always {@code true}.
     * 
     * @throws NullPointerException If {@code internalElement} is {@code null}.
     * @throws IllegalArgumentException If {@code internalElement} is not owned by this
     *         queue.
     */
    public boolean rawOffer(HashQueueElement<E> internalElement) {
        return rawAdd(internalElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#remove()
     */
    @Override
    public E remove() {
        return rawRemove().value;
    }

    /**
     * Retrieves and removes the internal representation of the head of this queue.
     * This method differs from {@link #rawPoll()} only in that it throws an exception if
     * this queue is empty.
     * 
     * @return The internal representation of the head of this queue.
     * 
     * @throws NoSuchElementException If this queue is empty.
     */
    public HashQueueElement<E> rawRemove() {
        HashQueueElement<E> polled = rawPoll();
        if (polled == null) {
            throw new NoSuchElementException();
        }
        return polled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#poll()
     */
    @Override
    public E poll() {
        HashQueueElement<E> polled = rawPoll();
        if (polled == null) {
            return null;
        }
        return polled.value;
    }

    /**
     * Retrieves and removes the internal representation of the head of this queue, or 
     * returns {@code null} if this queue is empty.
     * 
     * @return The internal representation of the head of this queue, or {@code null} if 
     *         this queue is empty
     */
    public HashQueueElement<E> rawPoll() {
        HashQueueElement<E> polled = head;
        rawRemove(polled);
        return polled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#element()
     */
    @Override
    public E element() {
        return rawElement().value;
    }

    /**
     * Retrieves, but does not remove, the internal representation of the head of this queue.
     * This method differs from {@link #rawPeek()} only in that it throws an exception if
     * this queue is empty.
     * 
     * @return The internal representation of the head of this queue.
     * 
     * @throws NoSuchElementException If this queue is empty.
     */
    public HashQueueElement<E> rawElement() {
        HashQueueElement<E> rawPeek = rawPeek();
        if (rawPeek == null) {
            throw new NoSuchElementException();
        }
        return rawPeek;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Queue#peek()
     */
    @Override
    public E peek() {
        HashQueueElement<E> rawPeek = rawPeek();
        if (rawPeek == null) {
            return null;
        }
        return rawPeek.value;
    }

    /**
     * Retrieves, but does not remove, the internal representation of the head of this 
     * queue, or returns {@code null} if this queue is empty.
     * 
     * @return The internal representation of the head of this queue, or {@code null} 
     *         if this queue is empty
     */
    public HashQueueElement<E> rawPeek() {
        return head;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        if (head == null) {
            return Collections.emptyIterator();
        }
        return iterator(head.value);
    }

    /**
     * Gives an iterator over the elements of this queue, starting from the given one.
     * 
     * @param first The element to start the iteration from.
     * 
     * @return The iterator over the elements of this queue, starting from {@code first}.
     */
    public Iterator<E> iterator(E first) {
        return new Iterator<>() {

            /**
             * The adapted raw iterator.
             */
            private Iterator<HashQueueElement<E>> raw = rawIterator(cache.get(first));

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#hasNext()
             */
            @Override
            public boolean hasNext() {
                return raw.hasNext();
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#next()
             */
            @Override
            public E next() {
                return raw.next().value;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#remove()
             */
            @Override
            public void remove() {
                raw.remove();
            }
            
        };
    }

    /**
     * Gives an iterator over the internal representation of the elements of this queue.
     * 
     * @return The iterator over the elements of this queue.
     */
    public Iterator<HashQueueElement<E>> rawIterator() {
        return rawIterator(head);
    }

    /**
     * Gives an iterator over the internal representation of the elements of this queue,
     * starting from the given one.
     * 
     * @param first The element to start the iteration from.
     * 
     * @return The iterator over the elements of this queue, starting from {@code first}.
     * 
     * @throws IllegalArgumentException If {@code first} is not owned by this queue.
     */
    public Iterator<HashQueueElement<E>> rawIterator(HashQueueElement<E> first) {
        ensureOwned(first);

        return new Iterator<>() {

            /**
             * The element previously considered.
             */
            private HashQueueElement<E> previous = null;

            /**
             * The next element to consider.
             */
            private HashQueueElement<E> next = first;

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#hasNext()
             */
            @Override
            public boolean hasNext() {
                return next != null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#next()
             */
            @Override
            public HashQueueElement<E> next() {
                if (hasNext()) {
                    previous = next;
                    next = next.next;
                    return previous;
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
                HashQueueElement<E> current = previous;
                previous = current.previous;
                rawRemove(current);
            }
            
        };
    }

    /**
     * Ensures that the given internal representation of an element is owned by this queue, 
     * and throws an {@link IllegalArgumentException} otherwise.
     * 
     * @param internalElement The internal representation to check.
     * 
     * @throws IllegalArgumentException If {@code internalElement} is not owned by this
     *         queue.
     */
    private void ensureOwned(HashQueueElement<E> internalElement) {
        if ((internalElement != null) && (internalElement.owner != this)) {
            throw new IllegalArgumentException("This element is not part of this queue!");
        }
    }

}
