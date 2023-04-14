package org.myshelfie.network;

public interface Listener<T extends Enum<T>> {
    public void update(T ev, Object arg);
}
