package ru.ifmo.ctddev.zenkova.concurrent;

import java.util.Comparator;
import java.util.List;

/**
 * Worker that finds first maximum element in the list.
 *
 * @param <T> type parameter.
 * @see ru.ifmo.ctddev.zenkova.concurrent.Worker
 */
public class Maximum<T> extends LazyWorker<T> {
    private Minimum<T> minimumWorker;

    public Maximum(List<? extends T> list, Comparator<? super T> comparator) {
        super();
        minimumWorker = new Minimum<>(list, comparator.reversed());
    }

    public T calcResult() {
        return minimumWorker.getResult();
    }

    @Override
    public T mergeResults(List<T> results) {
        return minimumWorker.mergeResults(results);
    }
}
