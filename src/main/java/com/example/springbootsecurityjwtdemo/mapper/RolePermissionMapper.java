package com.example.springbootsecurityjwtdemo.mapper;

import com.example.springbootsecurityjwtdemo.bean.dto.RolePermissionDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper {
    @Select("SELECT a.`name` as name , b.url as url FROM roles as a INNER JOIN role_permission as b ON a.id = b.roleid")
    List<RolePermissionDto> selectAllRolePermissionDto();
    @Insert("INSERT INTO role_permission (roleid,url) VALUES(#{roleid},#{url})")
    int insertRolePermission(long roleid,String url);
    @Select("SELECT a.`name` as name FROM roles as a INNER JOIN role_permission as b ON a.id = b.roleid where b.url = #{url}")
    List<String> selectNameFromUrl(String url);

}
