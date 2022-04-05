package com.example.springbootsecurityjwtdemo.security;


import com.example.springbootsecurityjwtdemo.mapper.RolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class DynamicAccessDecisionManger {
    @Autowired
    RolePermissionMapper rolePermissionMapper;
    @Bean
    public AccessDecisionManager dynamicAccessDecisionManager() {
        System.out.println(true);
        List<AccessDecisionVoter<? extends Object>> decisionVoters
                = Arrays.asList(
                dynamicVoter(),
                new WebExpressionVoter(),
                // new RoleVoter(),
                new AuthenticatedVoter());
        return new AffirmativeBased(decisionVoters);
    }

    @Bean
    public AccessDecisionVoter dynamicVoter(){
        return new AccessDecisionVoter<Object>() {
            @Override
            public boolean supports(ConfigAttribute attribute) {
                return true;
            }

            @Override
            public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
                if(authentication == null) {
                    return ACCESS_DENIED;
                }

                int result = ACCESS_ABSTAIN;
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                FilterInvocation fi = (FilterInvocation) object;
                String url = fi.getRequestUrl();
                 /*
                    例：url = "/sql/test"
                    具体思路 select * from role_permission where url = '/sql/test'
                    动态权限我进行确定查找，不进行模糊查找
                    如果你想进行模糊查找可以使用in的方法：SELECT * from role_permission WHERE url in ('/sql/test','/**','/*') ORDER BY url desc
                  */
                List<String> roleNameList = rolePermissionMapper.selectNameFromUrl(url);//这个url需要什么角色

                if(roleNameList!=null){
                    attributes = SecurityConfig.createList(roleNameList.toArray(new String[0]));
                }
                for (ConfigAttribute attribute : attributes) {
                    if(attribute.getAttribute()==null){
                        continue;
                    }
                    if (this.supports(attribute)) {
                        result = ACCESS_DENIED;

                        // Attempt to find a matching granted authority
                        for (GrantedAuthority authority : authorities) {
                            if (attribute.getAttribute().equals(authority.getAuthority())) {
                                return ACCESS_GRANTED;
                            }
                        }
                    }
                }

                return result;
            }

            @Override
            public boolean supports(Class clazz) {
                return true;
            }
        };
    }
}
