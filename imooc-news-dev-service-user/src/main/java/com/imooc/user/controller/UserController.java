package com.imooc.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imooc.api.controller.user.UserControllerApi;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.user.domain.AppUser;
import com.imooc.user.service.AppUserService;
import com.imooc.utils.JsonUtils;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import imooc.pojo.bo.UpdateUserInfoBO;
import imooc.pojo.vo.AppUserVO;
import imooc.pojo.vo.UserAccountInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import com.imooc.utils.RedisOperator;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.imooc.api.BaseController.getErrors;
import static com.imooc.api.interceptors.BaseInterceptor.REDIS_USER_INFO;

public class UserController implements UserControllerApi {

    final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AppUserService userService;
    @Resource
    private RedisOperator redis;
    @Override
    public GraceJSONResult getUserInfo(String userId) {
        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        // 1. 根据userId查询用户的信息
        AppUser user = getUser(userId);

        // 2. 返回用户信息
        AppUserVO userVO = new AppUserVO();
        BeanUtils.copyProperties(user, userVO);

//        // 3. 查询redis中用户的关注数和粉丝数，放入userVO到前端渲染
//        userVO.setMyFansCounts(getCountsFromRedis(REDIS_WRITER_FANS_COUNTS + ":" + userId));
//        userVO.setMyFollowCounts(getCountsFromRedis(REDIS_MY_FOLLOW_COUNTS + ":" + userId));

        return GraceJSONResult.ok(userVO);
    }

    @Override
    public GraceJSONResult getAccountInfo(String userId) {

        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        // 1. 根据userId查询用户的信息
        AppUser user = getUser(userId);

        // 2. 返回用户信息
        UserAccountInfoVO accountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user, accountInfoVO);

        return GraceJSONResult.ok(accountInfoVO);
    }

    private AppUser getUser(String userId) {
        // 查询判断redis中是否包含用户信息，如果包含，则查询后直接返回，就不去查询数据库了
        String userJson = redis.get(REDIS_USER_INFO + ":" + userId);
        AppUser user = null;
        if (StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        } else {
            user = userService.getById(userId);
            // 由于用户信息不怎么会变动，对于一些千万级别的网站来说，这类信息不会直接去查询数据库
            // 那么完全可以依靠redis，直接把查询后的数据存入到redis中
            redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
        }
        return user;
    }

    @Override
    public GraceJSONResult updateUserInfo(
            @Valid UpdateUserInfoBO updateUserInfoBO,
            BindingResult result) {

        // 0. 校验BO
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return GraceJSONResult.errorMap(map);
        }
        AppUser user = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO, user);
        // 1. 执行更新操作
        userService.updateById(user);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryByIds(String userIds) {

        if (StringUtils.isBlank(userIds)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        List<AppUserVO> publisherList = new ArrayList<>();
        List<String> userIdList = JsonUtils.jsonToList(userIds, String.class);
        for (String userId : userIdList) {
            // 获得用户基本信息
            AppUserVO userVO = getBasicUserInfo(userId);
            // 添加到publisherList
            publisherList.add(userVO);
        }

        return GraceJSONResult.ok(publisherList);
    }

    private AppUserVO getBasicUserInfo(String userId) {
        // 1. 根据userId查询用户的信息
        AppUser user = getUser(userId);

        // 2. 返回用户信息
        AppUserVO userVO = new AppUserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }
}
