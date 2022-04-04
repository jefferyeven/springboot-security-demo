package com.example.springbootsecurityjwtdemo.security;

import com.example.springbootsecurityjwtdemo.bean.dto.RolePermissionDto;
import com.example.springbootsecurityjwtdemo.mapper.RolePermissionMapper;
import com.example.springbootsecurityjwtdemo.mapper.RolesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class MyFilterSecurityInterceptor {

    @Autowired
    RolePermissionMapper rolePermissionMapper;
    @Autowired
    ApplicationContext ioc;

    @Bean
    public FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource(){
        //加载所有资源
        List<RolePermissionDto> list = rolePermissionMapper.selectAllRolePermissionDto();
        Map<String,Set<String>> setMap = new HashMap<>();
        for(RolePermissionDto rolePermissionDto: list){
            if(setMap.containsKey(rolePermissionDto.getUrl())){
                setMap.get(rolePermissionDto.getUrl()).add(rolePermissionDto.getName());
            }else {
                Set<String> temp = new HashSet<>();
                temp.add(rolePermissionDto.getName());
                setMap.put(rolePermissionDto.getUrl(),temp);
            }
        }
        return new FilterInvocationSecurityMetadataSource() {
            @Override
            public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
                if (object instanceof FilterInvocation){
                    FilterInvocation fi = (FilterInvocation) object;
                    for (String pattern : setMap.keySet()){
                        AntPathRequestMatcher matcher = new AntPathRequestMatcher(pattern);
                        if (matcher.matches(fi.getHttpRequest())){
                            return setMap.get(pattern).stream().map(n-> (ConfigAttribute) () -> n).collect(Collectors.toList());//返回url匹配的资源
                        }
                    }
                }
                return null;
            }

            @Override
            public Collection<ConfigAttribute> getAllConfigAttributes() {
                return null;
            }

            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }
        };
    }


    @Bean//配置FilterSecurityInterceptor
    public FilterSecurityInterceptor filterSecurityInterceptor(){
        RoleHierarchy roleHierarchy = (RoleHierarchy) ioc.getBean("roleHierarchy");
        List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<>();
        //用角色投票
        voters.add(new RoleVoter());
        //用可以继承的角色进行投票
        voters.add(new RoleHierarchyVoter(roleHierarchy));
        AccessDecisionManager accessDecisionManager = new AffirmativeBased(voters);

        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setAccessDecisionManager(accessDecisionManager);
        filterSecurityInterceptor.setSecurityMetadataSource(filterInvocationSecurityMetadataSource());
        filterSecurityInterceptor.setObserveOncePerRequest(false);
        return filterSecurityInterceptor;
    }

}
