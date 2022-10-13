package com.imooc.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.user.domain.AppUser;
import com.imooc.user.mapper.AppUserMapper;
import com.imooc.user.service.AppUserService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【app_user(网站用户)】的数据库操作Service实现
* @createDate 2022-10-13 20:35:46
*/
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser>
    implements AppUserService{

}




