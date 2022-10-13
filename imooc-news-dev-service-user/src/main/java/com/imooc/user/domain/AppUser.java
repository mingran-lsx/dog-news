package com.imooc.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 网站用户
 * @TableName app_user
 */
@TableName(value ="app_user")
@Data
public class AppUser implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称，媒体号
     */
    private String nickname;

    /**
     * 头像
     */
    private String face;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 性别 1:男  0:女  2:保密
     */
    private Integer sex;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 用户状态：0：未激活。 1：已激活：基本信息是否完善，真实姓名，邮箱地址，性别，生日，住址等，如果没有完善，则用户不能在作家中心操作，不能关注。2：已冻结。
     */
    private Integer active_status;

    /**
     * 累计已结算的收入金额，也就是已经打款的金额，每次打款后再此累加
     */
    private Integer total_income;

    /**
     * 创建时间 创建时间
     */
    private Date created_time;

    /**
     * 更新时间 更新时间
     */
    private Date updated_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
