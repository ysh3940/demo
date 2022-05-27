package com.shenhui.demo.concurrent.rpc;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.Function;

@Service
@Slf4j
public class BatchOperateServiceImpl implements BatchOperateService{

    @Resource
    private TracedExecutorService tracedExecutorService;

    @Override
    public <T, R> List<R> batchOperate(Function<T, R> function, List<T> requests, BatchOperateConfig config) {
        log.info("batchOperate start function:{} request:{} config:{}", function, JSON.toJSONString(requests), JSON.toJSONString(config));

        // 当前时间
        long startTime = System.currentTimeMillis();

        // 初始化
        int numberOfRequests = requests.size();

        // 所有异步线程执行结果
        List<Future<R>> futures = Lists.newArrayListWithExpectedSize(numberOfRequests);
        // 使用countDownLatch进行并发调用管理
        CountDownLatch countDownLatch = new CountDownLatch(numberOfRequests);
        List<BatchOperateCallable<T, R>> callables = Lists.newArrayListWithExpectedSize(numberOfRequests);

        // 分别提交异步线程执行
        for (T request : requests) {
            BatchOperateCallable<T, R> batchOperateCallable = new BatchOperateCallable<>(countDownLatch, function, request);
            callables.add(batchOperateCallable);

            // 提交异步线程执行
            Future<R> future = tracedExecutorService.submit(ThreadPoolName.RPC_EXECUTOR, batchOperateCallable);
            futures.add(future);
        }

        try {
            // 等待全部执行完成，如果超时且要求全部调用成功，则抛出异常
            boolean allFinish = countDownLatch.await(config.getTimeout(), config.getTimeoutUnit());
            if (!allFinish && config.getNeedAllSuccess()) {
                throw new RuntimeException("batchOperate timeout and need all success");
            }
            // 遍历执行结果，如果有的执行失败且要求全部调用成功，则抛出异常
            boolean allSuccess = callables.stream().map(BatchOperateCallable::isSuccess).allMatch(BooleanUtils::isTrue);
            if (!allSuccess && config.getNeedAllSuccess()) {
                throw new RuntimeException("some batchOperate have failed and need all success");
            }

            // 获取所有异步调用结果并返回
            List<R> result = Lists.newArrayList();
            for (Future<R> future : futures) {
                R r = future.get();
                if (Objects.nonNull(r)) {
                    result.add(r);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            double duration = (System.currentTimeMillis() - startTime) / 1000.0;
            log.info("batchOperate finish duration:{}s function:{} request:{} config:{}", duration, function, JSON.toJSONString(requests), JSON.toJSONString(config));
        }
    }
}
