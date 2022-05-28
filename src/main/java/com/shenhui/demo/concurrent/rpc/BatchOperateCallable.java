package com.shenhui.demo.concurrent.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class BatchOperateCallable<T, R> implements Callable<R> {

    private final CountDownLatch countDownLatch;

    private final Function<T, R> function;

    private final T request;

    private boolean success;

    public BatchOperateCallable(CountDownLatch countDownLatch, Function<T, R> function, T request) {
        this.countDownLatch = countDownLatch;
        this.function = function;
        this.request = request;
    }

    @Override
    public R call() {
        try {
            success = false;
            R result = function.apply(request);
            success = true;
            return result;
        } finally {
            countDownLatch.countDown();
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
