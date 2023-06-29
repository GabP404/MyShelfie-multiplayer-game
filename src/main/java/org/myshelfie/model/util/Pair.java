package org.myshelfie.model.util;

import java.io.Serializable;

/**
 * A simple generic class to hold a pair of objects
 * @param <L> Type of the left object
 * @param <R> Type of the right object
 */
public class Pair<L,R> implements Serializable {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        assert left != null;
        assert right != null;

        this.left = left;
        this.right = right;
    }

    public L getLeft() { return left; }
    public R getRight() { return right; }

    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
                this.right.equals(pairo.getRight());
    }

}