package com.example.springbootsecurityjwtdemo.mapper;

import com.example.springbootsecurityjwtdemo.bean.entity.Authorities;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuthoritiesMapper {
    @Select("SELECT roleid from authorities where userid = #{id}")
    List<Long> selectRoleidsFromUserid(long id);

    @Insert("INSERT INTO authorities (roleid,userid) VALUES( #{roleid}, #{userid})")
    int addAuthorites(Authorities authorities);
}
