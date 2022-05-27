package com.shenhui.demo.control;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.shenhui.demo.concurrent.rpc.BatchOperateConfig;
import com.shenhui.demo.concurrent.rpc.BatchOperateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@CrossOrigin
@RestController
public class BatchOperateServiceControl {

    @Autowired
    BatchOperateService batchOperateService;

    @GetMapping("/batchOperate")
    public String batchOperate() {
        Function<String, String> test1 = new Function<String, String>() {
            @Override
            public String apply(String s) {
                log.error("入参: {}", s);
                return "出参: "+s;
            }
        };

        List<String> list = batchOperateService.batchOperate(test1, Lists.newArrayList("1", "2"), BatchOperateConfig.builder()
                .timeout(10L).timeoutUnit(TimeUnit.SECONDS).needAllSuccess(false).build());

        return JSON.toJSONString(list);
    }

    @GetMapping("/singleOperate")
    public String singleOperate() throws ExecutionException, InterruptedException {
        Function<String, String> test1 = new Function<String, String>() {
            @Override
            public String apply(String s) {
                log.error("入参1: {}", s);
                return "出参1: "+s;
            }
        };

        Function<String, String> test2 = new Function<String, String>() {
            @Override
            public String apply(String s) {
                log.error("入参2: {}", s);
                return "出参2: "+s;
            }
        };

        Future<String> future1 = batchOperateService.singleOperate(test1, "1");
        Future<String> future2 = batchOperateService.singleOperate(test1, "2");

        while (true) {
            if (future1.isDone() && future2.isDone()) {
                break;
            }
        }

        return JSON.toJSONString(Lists.newArrayList(future1.get(), future2.get()));
    }

}
