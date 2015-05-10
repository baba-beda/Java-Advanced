package ru.ifmo.ctddev.zenkova.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private FixedThreadPool threadPool;

    public ParallelMapperImpl(int threads) {
        threadPool = new FixedThreadPool(threads);
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        List<R> resultList = new ArrayList<>(args.size());
        Counter counter = new Counter();

        for (int i = 0; i < args.size(); i++) {
            resultList.add(null);
            threadPool.execute(new Subtask(counter, new MapWorker<>(f, args.get(i), resultList, i)));
        }

        counter.waitFor(args.size());

        return resultList;
    }

    @Override
    public void close() throws InterruptedException {
        threadPool.shutdown();
    }
}
