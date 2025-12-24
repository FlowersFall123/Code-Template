package com.example.backend.entity.dto;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiOperation(value = "验证邮箱表单")
public class VerifyEmail {
    /**
     * 邮箱
     */
    private String email;
    /**
     * 验证码
     */
    private String code;
}
