package com.example.springbootsecurityjwtdemo.security;

import com.example.springbootsecurityjwtdemo.bean.dto.RolePermissionDto;
import com.example.springbootsecurityjwtdemo.mapper.RolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.util.AntPathMatcher;

import java.util.*;

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
            private final AntPathMatcher antPathMatcher = new AntPathMatcher();
            @Override
            public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
                if (object instanceof FilterInvocation){
                    FilterInvocation fi = (FilterInvocation) object;
                    String url = fi.getRequestUrl();
                    System.out.println(url);
                    for (String pattern : setMap.keySet()){
                        if(antPathMatcher.match(pattern,url)){
                            return SecurityConfig.createList(setMap.get(pattern).toArray(new String[0]));
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

    @Bean
    public FilterInvocationSecurityMetadataSource dynamicFilterInvocationSecurityMetadataSource(){

        return new FilterInvocationSecurityMetadataSource() {
            @Override
            public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
                if (object instanceof FilterInvocation){
                    FilterInvocation fi = (FilterInvocation) object;
                    String url = fi.getRequestUrl();
                    System.out.println(url);
                    /*
                    例：url = "/sql/test"
                    具体思路 select * from role_permission where url = '/sql/test'
                    动态权限我进行确定查找，不进行模糊查找
                    如果你想进行模糊查找可以使用in的方法：SELECT * from role_permission WHERE url in ('/sql/test','/**','/*') ORDER BY url desc
                     */
                    List<String> roleNameList = rolePermissionMapper.selectNameFromUrl(url);
                    if(roleNameList!=null){
                        return SecurityConfig.createList(roleNameList.toArray(new String[0]));
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
        // 不配置动态权限
        // filterSecurityInterceptor.setSecurityMetadataSource(filterInvocationSecurityMetadataSource());
        // 第一种设置动态权限
        filterSecurityInterceptor.setSecurityMetadataSource(dynamicFilterInvocationSecurityMetadataSource());
        filterSecurityInterceptor.setObserveOncePerRequest(false);
        return filterSecurityInterceptor;
    }

}
