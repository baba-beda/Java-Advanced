package ru.ifmo.ctddev.zenkova.mapper;

/**
 * AtomicInteger-like counter
 * @see java.util.concurrent.atomic.AtomicInteger
 */
public class Counter {
    private int count;

    public Counter() {
        count = 0;
    }

    public void increment() {
        synchronized (this) {
            count++;
        }
    }

    public void waitFor(int other) {
        while (true) {
            synchronized (this) {
                if (count == other) {
                    break;
                }
            }
        }
    }
}
