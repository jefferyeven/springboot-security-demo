package com.example.springbootsecurityjwtdemo.bean.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Users{
    private long id;
    private String username;
    private String password;
    private boolean enabled;
    public Users(){
        enabled = true;
    }
}
