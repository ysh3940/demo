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

    @Bean(name = ThreadPoolName.DEFAULT_EXECUTOR)
    public AsyncTaskExecutor baseExecutorService() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix(ThreadPoolName.DEFAULT_EXECUTOR + "--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setDaemon(Boolean.TRUE);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();

        return taskExecutor;
    }


    @Bean(name = ThreadPoolName.RPC_EXECUTOR)
    public AsyncTaskExecutor rpcExecutorService() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix(ThreadPoolName.RPC_EXECUTOR + "--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setDaemon(Boolean.TRUE);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();

        return taskExecutor;
    }

    public AsyncTaskExecutor fetchAsyncTaskExecutor(String name) {
        AsyncTaskExecutor executor = executorMap.get(name);
        if (executor == null) {
            throw new RuntimeException("no executor name " + name);
        }
        return executor;
    }
}
