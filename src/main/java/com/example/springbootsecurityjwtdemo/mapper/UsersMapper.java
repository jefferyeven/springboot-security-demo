package com.example.springbootsecurityjwtdemo.mapper;

import com.example.springbootsecurityjwtdemo.bean.entity.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface UsersMapper {
    @Select("select * from users where username=#{name}")
    Users selectUserByName(String name);
    @Insert("INSERT INTO users (username,password,enabled) VALUES (#{username},#{password},#{enabled})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int addUser(Users users);

}
