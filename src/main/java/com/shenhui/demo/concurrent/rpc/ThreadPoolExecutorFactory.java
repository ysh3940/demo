package com.shenhui.demo.concurrent.rpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolExecutorFactory {


    @Resource
    private Map<String, AsyncTaskExecutor> executorMap;

    /**
     * 默认的线程池
     */
    @Bean(name = ThreadPoolName.DEFAULT_EXECUTOR)
    public AsyncTaskExecutor baseExecutorService() {
        //后续支持各个服务定制化这部分参数
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //设置线程池参数信息
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix(ThreadPoolName.DEFAULT_EXECUTOR + "--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setDaemon(Boolean.TRUE);
        //修改拒绝策略为使用当前线程执行
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化线程池
        taskExecutor.initialize();

        return taskExecutor;
    }

    /**
     * 并发调用单独的线程池
     */
    @Bean(name = ThreadPoolName.RPC_EXECUTOR)
    public AsyncTaskExecutor rpcExecutorService() {
        //后续支持各个服务定制化这部分参数
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //设置线程池参数信息
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix(ThreadPoolName.RPC_EXECUTOR + "--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setDaemon(Boolean.TRUE);
        //修改拒绝策略为使用当前线程执行
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化线程池
        taskExecutor.initialize();

        return taskExecutor;
    }
    /**
     * 根据线程池名称获取线程池
     * 若找不到对应线程池，则抛出异常
     * @param name 线程池名称
     * @return 线程池
     * @throws RuntimeException 若找不到该名称的线程池
     */
    public AsyncTaskExecutor fetchAsyncTaskExecutor(String name) {
        AsyncTaskExecutor executor = executorMap.get(name);
        if (executor == null) {
            throw new RuntimeException("no executor name " + name);
        }
        return executor;
    }
}
