package com.imooc.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 粉丝表，用户与粉丝的关联关系，粉丝本质也是用户。
关联关系保存到es中，粉丝数方式和用户点赞收藏文章一样。累加累减都用redis来做。
字段与用户表有些冗余，主要用于数据可视化，数据一旦有了之后，用户修改性别和省份无法影响此表，只认第一次的数据。


 * @TableName fans
 */
@TableName(value ="fans")
@Data
public class Fans implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 作家用户id
     */
    private String writer_id;

    /**
     * 粉丝用户id
     */
    private String fan_id;

    /**
     * 粉丝头像
     */
    private String face;

    /**
     * 粉丝昵称
     */
    private String fan_nickname;

    /**
     * 粉丝性别
     */
    private Integer sex;

    /**
     * 省份
     */
    private String province;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
