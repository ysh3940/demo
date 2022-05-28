package com.shenhui.demo.concurrent.rpc;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Component
public class TracedExecutorService {

    @Resource
    private ThreadPoolExecutorFactory threadPoolExecutorFactory;


    public <T> Future<T> submit(String executorName, Callable<T> tracedCallable) {
        return threadPoolExecutorFactory.fetchAsyncTaskExecutor(executorName).submit(tracedCallable);
    }
}
