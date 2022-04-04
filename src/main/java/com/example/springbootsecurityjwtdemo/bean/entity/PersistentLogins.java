package com.example.springbootsecurityjwtdemo.bean.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class PersistentLogins {
 private  String username;
 private  String series;
 private  String tokenValue;
 private Date date;
    //省略 getter
}
