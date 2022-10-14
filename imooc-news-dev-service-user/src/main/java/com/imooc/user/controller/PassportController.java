package com.imooc.user.controller;

import com.imooc.api.controller.user.PassportControllerApi;
import com.imooc.enums.UserStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.user.domain.AppUser;
import com.imooc.user.service.AppUserService;
import com.imooc.utils.IPUtil;
import com.imooc.utils.RedisOperator;
import com.imooc.utils.SMSUtils;
import imooc.pojo.bo.RegistLoginBo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.imooc.api.BaseController.MOBILE_SMSCODE;
import static com.imooc.api.BaseController.getErrors;

@RestController
public class PassportController implements PassportControllerApi {
    @Resource
    private SMSUtils smsUtils;
    @Resource
    private RedisOperator redisOperator;
    @Resource
    private AppUserService appUserService;
    @Value("${website.domain-name}")
    private String domainName;
    private static final String REDIS_USER_TOKEN = "redis_user_token";
    private static final Integer COOKIE_MONTH = 30 * 24 * 60 * 60;
    @Override
    public GraceJSONResult getSMSCode(String mobile,HttpServletRequest request) {
        String userIp = IPUtil.getRequestIp(request);
        redisOperator.setnx60s(MOBILE_SMSCODE+":"+userIp,userIp);
        String random = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        String sms = smsUtils.sendSMS(mobile, random);
        redisOperator.set(MOBILE_SMSCODE+":"+mobile,random,30*60); // 30分钟
        return GraceJSONResult.ok(sms);
    }

    @Override
    public GraceJSONResult doLogin(RegistLoginBo registLoginBo , BindingResult result, HttpServletRequest request,
                                   HttpServletResponse response) throws ParseException {
        //0.判断BindingResult是否保存错误的验证信息，如果有，则需要返回
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
        }

        String mobile = registLoginBo.getMobile();
        String smsCode = registLoginBo.getSmsCode();
        String redisSMSCode = redisOperator.get(MOBILE_SMSCODE+":"+mobile);
        if(StringUtils.isBlank(mobile) || !redisSMSCode.equals(smsCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        AppUser user = appUserService.queryMobileIsExist(mobile);
        if(user != null&& Objects.equals(user.getActive_status(), UserStatus.FROZEN.type)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }else if(user==null){
            user = appUserService.createUser(mobile);
        }
        // 保存用户会话
        if(!Objects.equals(user.getActive_status(), UserStatus.FROZEN.type)){
            //保存token到redis
            String uToken = UUID.randomUUID().toString();
            redisOperator.set("redis_user_token:"+user.getId(),uToken);
            //保存用户id和token到cookie
            setCookie("utoken",uToken,response,COOKIE_MONTH);
            setCookie("uid", user.getId(), response,COOKIE_MONTH);
        }
        //用户登陆后，删除验证码
        redisOperator.del(MOBILE_SMSCODE+":"+mobile);
        return GraceJSONResult.ok(user.getActive_status());
    }




    public void setCookie(String key,String value,HttpServletResponse response,int maxAge){
        try {
            value = URLEncoder.encode(value,"utf-8");
            setCookieValue(key,value,response,maxAge);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Cookie cookie = new Cookie(key,value);
//
//        cookie.setDomain("localhost");
//        cookie.setPath("/");
//        cookie.setMaxAge(maxAge);

    }
    public void setCookieValue(String key,String value,HttpServletResponse response,int maxAge){

        Cookie cookie = new Cookie(key,value);

        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);

    }

}
