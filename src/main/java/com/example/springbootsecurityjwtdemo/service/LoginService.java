package com.example.springbootsecurityjwtdemo.service;

import com.example.springbootsecurityjwtdemo.bean.entity.Users;
import com.example.springbootsecurityjwtdemo.result.Response;
import com.example.springbootsecurityjwtdemo.result.ResponseData;
import com.example.springbootsecurityjwtdemo.result.ResponseMag;
import org.springframework.security.core.userdetails.User;

public interface LoginService {
    User lgoin(String name);
    Response register(String name, String password, String role);
}
