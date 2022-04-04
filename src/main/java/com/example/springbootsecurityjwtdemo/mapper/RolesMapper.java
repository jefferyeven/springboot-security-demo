package com.example.springbootsecurityjwtdemo.mapper;

import com.example.springbootsecurityjwtdemo.bean.entity.Roles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolesMapper {
    @Select("<script>" +
            "SELECT name from roles\n" +
            "        <where>\n" +
            "            <foreach item=\"item\" index=\"index\" collection=\"list\"\n" +
            "                     open=\"id in (\" separator=\",\" close=\")\">\n" +
            "                #{item}\n" +
            "            </foreach>\n" +
            "        </where>" +
            "</script>")
    List<String> selectRolesNameFromIds(List<Long> ids);
    @Select("SELECT * from roles where name = #{name}")
    Roles selectRolesFromName(String name);

}
