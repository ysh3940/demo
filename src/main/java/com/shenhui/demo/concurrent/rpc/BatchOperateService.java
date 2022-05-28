package com.shenhui.demo.concurrent.rpc;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

public interface BatchOperateService {

    <T, R> List<R> batchOperate(Function<T, R> function, List<T> requests, BatchOperateConfig config);

    <T, R> Future<R> singleOperate(Function<T, R> function, T request);


}
