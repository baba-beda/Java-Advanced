package ru.ifmo.ctddev.zenkova.arrayset;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by daria on 25.02.15.
 */
public class SetIterator<E> implements Iterator<E> {

    Iterator<E> eIterator;

    public SetIterator(List<E> list) {
        eIterator = list.iterator();
    }

    @Override
    public boolean hasNext() {
        return eIterator.hasNext();
    }

    @Override
    public E next() {
        return eIterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        eIterator.forEachRemaining(action);
    }
}
