package com.shenhui.demo.concurrent.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BatchOperateConfig {

    /**
     * 超时时间
     */
    private Long timeout;

    /**
     * 超时时间单位
     */
    private TimeUnit timeoutUnit;

    /**
     * 是否需要全部执行成功
     */
    private Boolean needAllSuccess;

}
