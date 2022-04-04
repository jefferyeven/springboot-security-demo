package com.example.springbootsecurityjwtdemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/admin/hello")
    public String admin() {
        return "admin";
    }

    @GetMapping("/user/hello")
    public String user() {
        return "user";
    }

    @GetMapping("/hello/v1")
    @PreAuthorize("hasAnyRole('admin')")
    public String helloV1(){
        return "test annotion";
    }

    @GetMapping("/sql/test")
    public String sqlTest(){
        return "sql test";
    }

    @GetMapping("/sql/testUser")
    public String sqlTestUser(){
        return "user sql test";
    }

}
