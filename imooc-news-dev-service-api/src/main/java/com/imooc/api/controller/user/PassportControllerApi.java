package com.imooc.api.controller.user;


import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.IMOOCJSONResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Api(tags = "用户注册登录的controller")
public interface PassportControllerApi {

    @GetMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String mobile);

}

