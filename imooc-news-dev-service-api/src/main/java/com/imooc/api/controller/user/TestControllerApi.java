package com.imooc.api.controller.user;


import com.imooc.grace.result.IMOOCJSONResult;
import org.springframework.web.bind.annotation.GetMapping;

public interface TestControllerApi {

    @GetMapping("test")
    public IMOOCJSONResult test();

}
