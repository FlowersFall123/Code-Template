package com.example.backend.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "登录表单")
public class LoginForm {
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = true)
    private String account;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}