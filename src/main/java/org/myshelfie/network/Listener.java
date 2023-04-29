package org.myshelfie.network;

public interface Listener<T extends Enum<T>> {
    void update(T ev, Object arg);
}
