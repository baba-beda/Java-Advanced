package ru.ifmo.ctddev.zenkova.concurrent;

import java.util.List;
import java.util.function.Predicate;

/**
 * Worker that checks that all list elements satisfy some predicate.
 *
 * @param <T> type parameter.
 * @see ru.ifmo.ctddev.zenkova.concurrent.Worker
 */
public class All<T> extends LazyWorker<Boolean> {
    private List<? extends T> list;
    private Predicate<? super T> predicate;

    public All(List<? extends T> list, Predicate<? super T> predicate) {
        super();
        this.list = list;
        this.predicate = predicate;
    }

    @Override
    public Boolean calcResult() {
        return list.stream()
                .reduce(true, (a, b) -> a && predicate.test(b), Boolean::logicalAnd);
    }

    @Override
    public Boolean mergeResults(List<Boolean> results) {
        return results.stream()
                      .reduce(true, Boolean::logicalAnd);
    }
}
