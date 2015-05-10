package ru.ifmo.ctddev.zenkova.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Iterative parallelism tools.
 *
 * @see info.kgeorgiy.java.advanced.concurrent.ScalarIP
 */
public class IterativeParallelism implements ScalarIP {
    private ParallelMapper mapper;

    public IterativeParallelism() {
        mapper = null;
    }

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Calculates first minimum of list in parallel.
     *
     * @param threads number of threads.
     * @param list list of jobs to be done.
     * @param comparator comparator used to compare list's elements to each other.
     * @param <T> list element's type.
     * @return first minimum of <tt>list</tt>.
     * @throws java.lang.InterruptedException if execution was interrupted.
     * @throws java.lang.IllegalArgumentException if number of threads is less than 1.
     */
    public <T> T minimum(int threads, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        return parallelize(threads, list, job -> new Minimum<>(job, comparator));
    }

    /**
     * Calculates first maximum of list in parallel.
     *
     * @param threads number of threads.
     * @param list list of jobs to be done.
     * @param comparator comparator used to compare list's elements to each other.
     * @param <T> list element's type.
     * @return first maximum of <tt>list</tt>.
     * @throws java.lang.InterruptedException if execution was interrupted.
     * @throws java.lang.IllegalArgumentException if number of threads is less than 1.
     */
    public <T> T maximum(int threads, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        return parallelize(threads, list, job -> new Maximum<>(job, comparator));
    }

    /**
     * Checks if all elements of list satisfy some predicate in parallel.
     *
     * @param threads number of threads.
     * @param list list of jobs to be done.
     * @param predicate predicate to check list elements with.
     * @param <T> list element's type.
     * @return true, if all elements of <tt>list</tt> satisfy <tt>predicate</tt>, false otherwise.
     * @throws java.lang.InterruptedException if execution was interrupted.
     * @throws java.lang.IllegalArgumentException if number of threads is less than 1.
     */
    public <T> boolean all(int threads, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return parallelize(threads, list, job -> new All<>(job, predicate));
    }

    /**
     * Checks if there exists element of list satisfying some predicate in parallel.
     *
     * @param threads number of threads.
     * @param list list of jobs to be done.
     * @param predicate predicate to check list elements with.
     * @param <T> list element's type.
     * @return true, if any element of <tt>list</tt> satisfies <tt>predicate</tt>, false otherwise.
     * @throws java.lang.InterruptedException if execution was interrupted.
     * @throws java.lang.IllegalArgumentException if number of threads is less than 1.
     */
    public <T> boolean any(int threads, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return parallelize(threads, list, job -> new Any<>(job, predicate));
    }


    private <T, R> R parallelize(int threads, List<T> job, Function<List<T>, Worker<R>> constructor) throws InterruptedException {
        return doInParallel(
                splitJob(threads, job)
                        .stream()
                        .map(constructor)
                        .collect(Collectors.toList())
        );
    }

    private <R> R doInParallel(List<Worker<R>> workers) throws InterruptedException {
        List<R> results;

        if (mapper != null) {
            // Run jobs and collect results.
            results = mapper.map(Worker::getResult, workers);
        } else {
            // Run jobs.
            List<Thread> threads = new ArrayList<>();
            for (Worker<R> worker : workers) {
                Thread thread = new Thread(worker);
                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // Collect results.
            results = workers.stream().map(Worker::getResult).collect(Collectors.toList());
        }

        // Merge and return results.
        return workers.get(0).mergeResults(results);
    }

    private static <T> List<List<T>> splitJob(int parts, List<T> job) {
        int jobSize = job.size();

        if (parts < 1) {
            throw new IllegalArgumentException("threads < 1");
        }

        int chunkSize;
        if (parts > jobSize) {
            chunkSize = 1;
        } else {
            chunkSize = jobSize / parts;
        }

        List<List<T>> jobs = new ArrayList<>();

        for (int l = 0; l < jobSize; l += chunkSize) {
            int r = Math.min(l + chunkSize, jobSize);
            jobs.add(job.subList(l, r));
        }

        return jobs;
    }
}
