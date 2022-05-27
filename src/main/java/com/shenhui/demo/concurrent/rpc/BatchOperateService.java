package com.shenhui.demo.concurrent.rpc;

import java.util.List;
import java.util.function.Function;

public interface BatchOperateService {

    /**
     * 并发批量操作
     * @param function 执行的逻辑
     * @param requests 请求
     * @param config 配置
     * @return 全部响应
     */
    <T, R> List<R> batchOperate(Function<T, R> function, List<T> requests, BatchOperateConfig config);

}
