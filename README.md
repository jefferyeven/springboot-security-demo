# 技术栈
spring security + swagger + mybaits + restful
## 简介
这个项目是我做前后端分离时候，后端用的鉴权框架，自定义了一些我任务我会需要的的一些框架的配置。
# 搭配 swagger
## pom
```java
        <!-- swagger -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-boot-starter</artifactId>
            <version>3.0.0</version>
        </dependency>
```
## 在启动类开启注解
@EnableOpenApi
## application.yaml
```java
# ===== 自定义swagger配置 ===== #
swagger:
  enable: true
  application-name: ${spring.application.name}
  application-version: 1.0
  application-description: springfox swagger 3.0整合Demo
  try-host: http://localhost:${server.port}
```
## 读取application配置
```java
@Component
@ConfigurationProperties("swagger")
@Getter
@Setter
public class SwaggerProperties {
    /**
     * 是否开启swagger，生产环境一般关闭，所以这里定义一个变量
     */
    private Boolean enable;
    /**
     * 项目应用名
     */
    private String applicationName;
    /**
     * 项目版本信息
     */
    private String applicationVersion;
    /**
     * 项目描述信息
     */
    private String applicationDescription;
    /**
     * 接口调试地址
     */
    private String tryHost;
}
```
## 编写配置类
```java
@Configuration
public class SwaggerConfiguration implements WebMvcConfigurer {
    private final SwaggerProperties swaggerProperties;

    public SwaggerConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public Docket createRestApi() {

        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 定义是否开启swagger，false为关闭，可以通过变量控制
                .enable(swaggerProperties.getEnable())

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())

                // 接口调试地址
                .host(swaggerProperties.getTryHost())

                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()

                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))

                // 授权信息全局应用
                .securityContexts(securityContexts());
    }

    /**
     * API 页面上半部分展示信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(swaggerProperties.getApplicationName() + " Api Doc")
                .description(swaggerProperties.getApplicationDescription())
                .contact(new Contact("lighter", null, "123456@gmail.com"))
                .version("Application Version: " + swaggerProperties.getApplicationVersion() + ", Spring Boot Version: " + SpringBootVersion.getVersion())
                .build();
    }


    /**
     * 授权信息全局应用
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(Collections.singletonList(new SecurityReference("BASE_TOKEN", new AuthorizationScope[]{new AuthorizationScope("global", "")})))
                        .build()
        );
    }
    @SafeVarargs
    private final <T> Set<T> newHashSet(T... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }

}
```
# 配置mybaits
这里我就不讲了，可以直接看最后gitee地址
# 配置 spring security
## pom
```java
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
```
## 配置类
在这里我主要自定义了以下配置：

1. 自定义controller, 通过authenticationManager 和UsernamePasswordAuthenticationToken （比较方便后面加一些验证码，或者记录登录日志之类的事情）
1. 从数据库读取角色的权限，即通过url配置权限
1. 记录登录rememberMe()（通过mysql记录登录）

这里我是直接使用他的默认的一个配置。
### 登录逻辑

1. 这里我没有使用spring security 自带的那个formLogin()模块，而访问自定义controller

这里的逻辑是将Authentication 加入上下文中。
```java
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
```

2. 自定义 userdetailservice
```java
@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    LoginService loginService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = loginService.lgoin(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }
}
```
### 权限逻辑

1. 表达式控制 URL 路径权限

在config类设置url权限
```java
.antMatchers("/admin/**").hasRole("admin") .antMatchers("/user/**").hasRole("user")
```

2. 通过注解配置权限

在启动类开启注解
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
在congtroller上加注解
```java
    @GetMapping("/hello/v1")
    @PreAuthorize("hasAnyRole('admin')")
    public String helloV1(){
        return "test annotion";
    }
```

3. 在sql设置url的访问权限

这里我主要使用FilterSecurityInterceptor来实现主要自定义AccessDecisionManager(这里是设置判断该用户的权限是否可以访问这次request url）和SecurityMetadataSource（这里设置什么能访问该权限）

- 设置AccessDecisionManager
```java
        RoleHierarchy roleHierarchy = (RoleHierarchy) ioc.getBean("roleHierarchy");
        List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<>();
        //用角色投票
        voters.add(new RoleVoter());
        //用可以继承的角色进行投票
        voters.add(new RoleHierarchyVoter(roleHierarchy));
        AccessDecisionManager accessDecisionManager = new AffirmativeBased(voters);
```

- 设置SecurityMetadataSource

从数据库里找到所有的url,以及每一个url所对应的多个权限
注意这里不是动态更新，只有第一次启动的时候才会访问数据库，要想动态更新数据库将数据库获取权限的逻辑放在（）
return new FilterInvocationSecurityMetadataSource() {
/*这里写获取数据库的逻辑*/
}
注释：我不推荐动态权限，你如果使用了动态权限，那么你每次访问一个链接都会查询一次数据库，比较浪费。
```java
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
```
### 动态权限
#### 第一种
第一种方法自定义FilterSecurityInterceptor，
具体原理，每一次访问链接时都会访问SecurityMetadataSource，我们会返回该允许的权限

```java
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

```
然后在security config类加上
```java
   @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**");
        web.securityInterceptor((FilterSecurityInterceptor) ioc.getBean("filterSecurityInterceptor"));
    }

```
#### 第二种
是使用自定义的voter
```java
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

```
然后在security 配置类
```java
.anyRequest().authenticated()
.accessDecisionManager(accessDecisionManager)  //根据voter配置动态权限
```
# 项目地址
[https://gitee.com/jefferyeven/springboot-security-demo](https://gitee.com/jefferyeven/springboot-security-demo)
# 运行步骤
1.在mysql导入sql文件
2.修改配置中数据库名
3.导入maven
