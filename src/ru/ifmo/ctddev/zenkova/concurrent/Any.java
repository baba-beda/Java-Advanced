package ru.ifmo.ctddev.zenkova.concurrent;

import java.util.List;
import java.util.function.Predicate;

/**
 * Worker that checks that there exists list element satisfying some predicate.
 *
 * @param <T> type parameter.
 * @see ru.ifmo.ctddev.zenkova.concurrent.Worker
 * @see ru.ifmo.ctddev.zenkova.concurrent.All
 */
public class Any<T> extends LazyWorker<Boolean> {
    private All<T> allWorker;

    public Any(List<? extends T> list, Predicate<? super T> predicate) {
        super();
        allWorker = new All<>(list, predicate.negate());
    }

    @Override
    public Boolean calcResult() {
        return !allWorker.getResult();
    }

    @Override
    public Boolean mergeResults(List<Boolean> results) {
        return results.stream()
                .reduce(false, Boolean::logicalOr);
    }
}
