package ru.ifmo.ctddev.zenkova.concurrent;

import java.util.Comparator;
import java.util.List;

/**
 * Worker that finds first minimum element in the list.
 *
 * @param <T> type parameter.
 * @see ru.ifmo.ctddev.zenkova.concurrent.Worker
 * @see ru.ifmo.ctddev.zenkova.concurrent.Maximum
 */
public class Minimum<T> extends LazyWorker<T> {
    private List<? extends T> list;
    private Comparator<? super T> comparator;

    public Minimum(List<? extends T> list, Comparator<? super T> comparator) {
        super();
        this.list = list;
        this.comparator = comparator;
    }

    @Override
    public T calcResult() {
        return list.stream()
                   .min(comparator)
                   .get();
    }

    @Override
    public T mergeResults(List<T> results) {
        return new Minimum<>(results, comparator).getResult();
    }
}
