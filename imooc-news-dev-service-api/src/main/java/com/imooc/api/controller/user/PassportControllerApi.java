package com.imooc.api.controller.user;


import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.IMOOCJSONResult;
import imooc.pojo.bo.RegistLoginBo;
import io.swagger.annotations.Api;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;

@RequestMapping("passport")
@Api(tags = "用户注册登录")
public interface PassportControllerApi {

    @GetMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String mobile, HttpServletRequest request);
    @PostMapping("/doLogin")
    public GraceJSONResult doLogin(@RequestBody @Valid RegistLoginBo registLoginBo ,
                                   BindingResult result, HttpServletRequest request,
                                   HttpServletResponse response) throws ParseException;
}

