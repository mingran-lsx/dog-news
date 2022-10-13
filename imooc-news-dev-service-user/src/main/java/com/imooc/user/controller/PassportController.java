package com.imooc.user.controller;

import com.imooc.api.controller.user.PassportControllerApi;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.utils.SMSUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class PassportController implements PassportControllerApi {
    @Resource
    private SMSUtils smsUtils;
    @Override
    public GraceJSONResult getSMSCode(String mobile) {
        String sms = smsUtils.sendSMS(mobile, "123456");
        return GraceJSONResult.ok(sms);
    }
}
