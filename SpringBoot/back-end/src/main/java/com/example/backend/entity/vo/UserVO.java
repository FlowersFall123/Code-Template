package com.example.backend.entity.vo;

/*
 * @Auther:fz
 * @Date:2025/9/5
 * @Description:
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private Long id;
    private String username,account,password,avatar,email;
    private Date time;
}
