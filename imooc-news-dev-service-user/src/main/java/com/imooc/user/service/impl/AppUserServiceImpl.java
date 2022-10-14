package com.imooc.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.enums.Sex;
import com.imooc.enums.UserStatus;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.user.domain.AppUser;
import com.imooc.user.mapper.AppUserMapper;
import com.imooc.user.service.AppUserService;
import com.imooc.utils.DesensitizationUtil;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
 * @description 针对表【app_user(网站用户)】的数据库操作Service实现
 * @createDate 2022-10-13 20:35:46
 */
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser>
        implements AppUserService {
    @Resource
    private AppUserMapper appUserMapper;
    @Resource
    private Sid sid;


    /**
     * 根据用户名查询用户 如果查询到了，返回用户对象
     *
     * @param mobile
     * @return
     */
    @Override
    public AppUser queryMobileIsExist(String mobile) {
        QueryWrapper<AppUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        AppUser appUser = appUserMapper.selectOne(queryWrapper);
        if (appUser != null) {
            return appUser;
        }
        return null;
    }

    @Transactional
    @Override
    public AppUser createUser(String mobile) throws ParseException {
        String userId = sid.nextShort();
        AppUser appUser = new AppUser();
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
        Date date = sdf.parse(" 2008-07-10 19:20:00 ");
        appUser.setId(userId);
        appUser.setMobile(mobile);
        appUser.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        appUser.setFace("http://t.cn/RCzsdCq");
        appUser.setBirthday(date);
        appUser.setSex(Sex.secret.type);
        appUser.setActive_status(UserStatus.INACTIVE.type);
        appUser.setTotal_income(0);
        appUser.setCreated_time(new Date());
        appUser.setUpdated_time(new Date());
        appUserMapper.insert(appUser);
        return appUser;
    }
}




