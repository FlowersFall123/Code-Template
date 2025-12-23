package com.example.backend.entity.po;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "用户实体类")
public class User {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String account;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 身份
     * admin:管理员
     * user:用户
     * up:博主
     * 默认值： user
     */
    @ApiModelProperty(value = "身份")
    private String identity;

    /**
     * 注册时间
     */
    @ApiModelProperty(value = "注册时间")
    private Date time;

    /**
     * 状态
     * 0:禁用
     * 1:正常
     * -1:注销
     * 默认值： 1
     */
    @ApiModelProperty(value = "状态")
    private Long status;
}