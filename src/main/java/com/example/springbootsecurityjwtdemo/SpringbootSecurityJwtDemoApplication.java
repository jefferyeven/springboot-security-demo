package com.example.springbootsecurityjwtdemo;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SpringbootSecurityJwtDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSecurityJwtDemoApplication.class, args);
    }

}
