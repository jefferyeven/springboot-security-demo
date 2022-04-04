package com.example.springbootsecurityjwtdemo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "test")
@RequestMapping("test")
public class TestController {
    @GetMapping("hello")
    public String hello(){
        return "hello world";
    }
    @GetMapping("testExceptionHandler")
    public String testExceptionHandler(){
        int b = 5/0;
        return "hello world";
    }

    @GetMapping("index")
    public String index(){
        return "index";
    }
}
