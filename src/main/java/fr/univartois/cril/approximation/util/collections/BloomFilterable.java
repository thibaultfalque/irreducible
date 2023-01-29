/*
 * This file is a part of the fr.univartois.cril.orpheus.utils.collections package.
 *
 * It contains the BloomFilterable interface, which allows an object to have multiple hash
 * codes to enable the use of Bloom filters.
 *
 * (c) Romain WALLON - Orpheus.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.util.collections;

/**
 * The BloomFilterable interface allows an object to have multiple hash codes to
 * enable the use of Bloom filters.
 *
 * @author Romain WALLON
 *
 * @version 1.0
 */
@FunctionalInterface
public interface BloomFilterable {
    
    /**
     * Gives the hash code values of this object.
     * A different array must be returned each time this method is called.
     * 
     * @return The array of hash codes.
     */
    int[] hashCodes();
    
    /**
     * Gives the hash code values of this object.
     * A different array must be returned each time this method is called.
     * 
     * @param modulo The value modulo which the hash codes must be computed.
     * 
     * @return The array of hash codes.
     *         All {@code h} in the returned array verify {@code 0 <= h < modulo}.
     *         
     * @implSpec The default implementation applies the modulo to the values obtained
     *           by calling {@link #hashCodes()}.
     *           
     * @see #hashCodes()
     */
    default int[] hashCodes(int modulo) {
        int[] hashCodes = hashCodes();
        
        for (int i = 0; i < hashCodes.length; i++) {
            hashCodes[i] %= modulo;
            if (hashCodes[i] < 0) {
                hashCodes[i] += modulo;
            }
        }
        
        return hashCodes;
    }

}
