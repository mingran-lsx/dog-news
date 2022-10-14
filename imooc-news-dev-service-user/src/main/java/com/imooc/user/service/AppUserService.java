package com.imooc.user.service;

import com.imooc.user.domain.AppUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.text.ParseException;

/**
* @author Administrator
* @description 针对表【app_user(网站用户)】的数据库操作Service
* @createDate 2022-10-13 20:35:46
*/
public interface AppUserService extends IService<AppUser> {

    AppUser queryMobileIsExist(String mobile);

    AppUser createUser(String mobile) throws ParseException;
}
