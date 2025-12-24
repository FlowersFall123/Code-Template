package com.example.backend.mapper;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import com.example.backend.entity.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 登录
     * @param email
     * @return
     */
    @Select("SELECT * FROM user WHERE email=#{email} ")
    User getUserByEmail(String email);

    /**
     * 注册用户
     * @param user
     * @return
     */
    Integer register(User user);

    /**
     * 获取所有用户
     * @return
     */
    @Select("SELECT * FROM user")
    List<User> getAllUser();

    /**
     * 获取用户
     * @param id
     * @return
     */
    @Select("SELECT * FROM user WHERE id=#{id}")
    User getUserById(Long id);
}
