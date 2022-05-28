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
    public <T, R> Future<R> singleOperate(Function<T, R> function, T request, CountDownLatch countDownLatch) {
        log.error("singleOperate start function:{} request:{}", function, JSON.toJSONString(request));

        BatchOperateCallable<T, R> batchOperateCallable = new BatchOperateCallable<>(countDownLatch, function, request);

        Future<R> future = tracedExecutorService.submit(ThreadPoolName.RPC_EXECUTOR, batchOperateCallable);

        return future;
    }

    @Override
    public <T, R> List<R> batchOperate(Function<T, R> function, List<T> requests, BatchOperateConfig config) {
        log.error("batchOperate start function:{} request:{} config:{}", function, JSON.toJSONString(requests), JSON.toJSONString(config));

        long startTime = System.currentTimeMillis();

        if (requests == null || requests.size() == 0) {
            return Lists.newArrayList();
        }
        int numberOfRequests = requests.size();

        List<Future<R>> futures = Lists.newArrayListWithExpectedSize(numberOfRequests);
        CountDownLatch countDownLatch = new CountDownLatch(numberOfRequests);
        List<BatchOperateCallable<T, R>> callables = Lists.newArrayListWithExpectedSize(numberOfRequests);

        for (T request : requests) {
            BatchOperateCallable<T, R> batchOperateCallable = new BatchOperateCallable<>(countDownLatch, function, request);
            callables.add(batchOperateCallable);

            Future<R> future = tracedExecutorService.submit(ThreadPoolName.RPC_EXECUTOR, batchOperateCallable);
            futures.add(future);
        }

        try {
            boolean allFinish = countDownLatch.await(config.getTimeout(), config.getTimeoutUnit());
            if (!allFinish && config.getNeedAllSuccess()) {
                log.error("batchOperate timeout and need all success");
                return Lists.newArrayList();
            }
            boolean allSuccess = callables.stream().map(BatchOperateCallable::isSuccess).allMatch(BooleanUtils::isTrue);
            if (!allSuccess && config.getNeedAllSuccess()) {
                log.error("some batchOperate have failed and need all success");
                return Lists.newArrayList();
            }

            List<R> result = Lists.newArrayList();
            for (Future<R> future : futures) {
                R r = future.get();
                if (Objects.nonNull(r)) {
                    result.add(r);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("batchOperate exception :{}", e.getMessage(), e);
        } finally {
            double duration = (System.currentTimeMillis() - startTime) / 1000.0;
            log.error("batchOperate finish duration:{}s function:{} request:{} config:{}", duration, function, JSON.toJSONString(requests), JSON.toJSONString(config));
        }
        return Lists.newArrayList();
    }


}
