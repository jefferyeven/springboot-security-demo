package com.example.springbootsecurityjwtdemo.service.impl;

import com.example.springbootsecurityjwtdemo.bean.entity.Authorities;
import com.example.springbootsecurityjwtdemo.bean.entity.Roles;
import com.example.springbootsecurityjwtdemo.bean.entity.Users;
import com.example.springbootsecurityjwtdemo.mapper.AuthoritiesMapper;
import com.example.springbootsecurityjwtdemo.mapper.RolesMapper;
import com.example.springbootsecurityjwtdemo.mapper.UsersMapper;
import com.example.springbootsecurityjwtdemo.result.Response;
import com.example.springbootsecurityjwtdemo.result.ResponseMag;
import com.example.springbootsecurityjwtdemo.result.ResponseUtil;
import com.example.springbootsecurityjwtdemo.service.LoginService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    UsersMapper usersMapper;
    @Autowired
    AuthoritiesMapper authoritiesMapper;
    @Autowired
    RolesMapper rolesMapper;
    PasswordEncoder getPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public User lgoin(String name) {
        Users users = usersMapper.selectUserByName(name);
        System.out.println(users.getId()+" "+users.getUsername());
        List<Long> roleids = authoritiesMapper.selectRoleidsFromUserid(users.getId());
        System.out.println(roleids.toString());
        List<String> roleNames = rolesMapper.selectRolesNameFromIds(roleids);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for(String s:roleNames){
            authorities.add(new SimpleGrantedAuthority(s));
        }
        return new User(users.getUsername(),users.getPassword(),authorities);
    }

    @Override
    public Response register(String name, String password, String role) {

        Users users = usersMapper.selectUserByName(name);
        if(users!=null){
            return ResponseUtil.returnResponse(ResponseMag.RepeatUserError);
        }
        Roles roles = rolesMapper.selectRolesFromName("ROLE_"+role);
        if(roles==null){
            return ResponseUtil.returnResponse(ResponseMag.NoRoleError);
        }
        password = getPasswordEncoder.encode(password);
        users = new Users();
        users.setUsername(name);
        users.setPassword(password);
        usersMapper.addUser(users);
        long userid = users.getId();
        Authorities authorities = new Authorities();
        authorities.setRoleid(roles.getId());
        authorities.setUserid(userid);
        authoritiesMapper.addAuthorites(authorities);
        return ResponseUtil.returnResponse(ResponseMag.SUCCESS);
    }
}
