package ru.ifmo.ctddev.zenkova.concurrent;

import java.util.Optional;

/**
 * Worker interface implementation with memorization of result.
 *
 * @param <R> result type.
 * @see ru.ifmo.ctddev.zenkova.concurrent.Worker
 */
public abstract class LazyWorker<R> implements Worker<R> {
    private Optional<R> result;

    public LazyWorker() {
        result = Optional.empty();
    }

    public void run() {
        getResult();
    }

    public R getResult() {
        if (!result.isPresent()) {
            result = Optional.of(calcResult());
        }

        return result.get();
    }

    /**
     * Returns the result of thread's work.
     *
     * @return result of {@link java.lang.Runnable#run() run()} method.
     */
    protected abstract R calcResult();
}