package org.myshelfie.network;

public interface Listener {
    public <E extends Enum<E>> void update(E ev, Object arg);
}
