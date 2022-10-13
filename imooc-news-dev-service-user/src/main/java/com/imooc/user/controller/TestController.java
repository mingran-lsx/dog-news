package com.imooc.user.controller;

import com.imooc.api.controller.user.TestControllerApi;
import com.imooc.grace.result.IMOOCJSONResult;
import com.imooc.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class TestController implements TestControllerApi {
    @Resource
    private RedisOperator redisOperator;
    public IMOOCJSONResult test() {
        return IMOOCJSONResult.ok();
    }
    @GetMapping("/redis")
    public IMOOCJSONResult redis() {
        redisOperator.set("imooc-cache", "hello~");
        String str = redisOperator.get("imooc-cache");
        log.info("redis中的值为:{}", str);
        return IMOOCJSONResult.ok(str);
    }

}
