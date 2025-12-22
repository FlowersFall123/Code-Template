package com.springCloud.userService.mapper;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */


import com.springCloud.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE account=#{account} AND password=#{password}")
    User getUserByAccountAndPassword(String account, String password);


    @Select("SELECT * FROM user WHERE id=#{id}")
    User getUserById(Long id);

}
