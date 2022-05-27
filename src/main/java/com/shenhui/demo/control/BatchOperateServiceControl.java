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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@CrossOrigin
@RestController
public class BatchOperateServiceControl {

    @Autowired
    BatchOperateService batchOperateService;

    @GetMapping("/batchOperateServiceControl")
    public String batchOperateServiceControl() {
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

}
