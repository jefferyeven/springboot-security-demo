package com.example.springbootsecurityjwtdemo.controller;

import com.example.springbootsecurityjwtdemo.mapper.UsersMapper;
import com.example.springbootsecurityjwtdemo.result.Response;
import com.example.springbootsecurityjwtdemo.result.ResponseMag;
import com.example.springbootsecurityjwtdemo.result.ResponseUtil;
import com.example.springbootsecurityjwtdemo.security.SecurityConfig;
import com.example.springbootsecurityjwtdemo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController

public class LoginController {
    @Autowired
    LoginService loginService;
    @Autowired
    SecurityConfig securityConfig;
    @Autowired
    ApplicationContext applicationContext;
    @GetMapping("login")
    public Response login(String name,String password){
        try {
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    name, password);
            AuthenticationManager authenticationManager = (AuthenticationManager) applicationContext.getBean("authenticationManagerBean");
            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e ){
            return ResponseUtil.returnResponse(ResponseMag.LoginError);
        }
        return ResponseUtil.returnResponse(ResponseMag.SUCCESS);
    }

    @PutMapping("register")
    public Response register(String name, String password, String role){

        return loginService.register(name,password,role);
    }

}
