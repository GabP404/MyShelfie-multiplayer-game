package org.myshelfie.model;

import org.junit.jupiter.api.Test;
import org.myshelfie.model.util.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test class for the Pair class
 */
public class PairTest {

    @Test
    public void testEquals() {
        Pair<Integer, Integer> pair1 = new Pair<>(1, 2);
        Pair<Integer, Integer> pair2 = new Pair<>(1, 2);
        Pair<Integer, Integer> pair3 = new Pair<>(2, 1);
        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);
    }

    @Test
    public void testHashCode() {
        Pair<Integer, Integer> pair1 = new Pair<>(1, 2);
        Pair<Integer, Integer> pair2 = new Pair<>(1, 2);
        Pair<Integer, Integer> pair3 = new Pair<>(2, 1);
        assertEquals(pair1.hashCode(), pair2.hashCode());
        assertEquals(pair1.hashCode(), pair3.hashCode());
    }
}
