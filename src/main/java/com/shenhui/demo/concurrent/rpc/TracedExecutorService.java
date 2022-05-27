package com.shenhui.demo.concurrent.rpc;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Component
public class TracedExecutorService {

    @Resource
    private ThreadPoolExecutorFactory threadPoolExecutorFactory;


    /**
     * 指定线程池提交异步任务，并获得任务上下文
     * @param executorName 线程池名称
     * @param tracedCallable 异步任务
     * @param <T> 返回类型
     * @return 线程上下文
     */
    public <T> Future<T> submit(String executorName, Callable<T> tracedCallable) {
        return threadPoolExecutorFactory.fetchAsyncTaskExecutor(executorName).submit(tracedCallable);
    }
}
