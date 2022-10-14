# dog-news

# 模块分层

- user
- api
- common
- model
- service

# 引入依赖

- spring
- mysql
- 

# 封装返回参数类

- ResultObject
- 状态码

# 修改user的application.yml文件

```
############################################################
#
# 用户微服务
# web访问端口号  约定：8003
#
############################################################
#tomcat:
#  uri-encoding: UTF-8
#  max-swallow-size: -1  # tomcat默认大小2M，超过2M的文件不会被捕获，需要调整此处大小为100MB或者-1即可
server:
#    port: 8003

############################################################
#
# 配置项目信息
#
############################################################
spring:
  profiles:
    active: dev   # yml中配置文件的环境配置，dev：开发环境，test：测试环境，prod：生产环境
  application:
    name: service-user
  datasource:                                         # 数据源的相关配置
#    type: com.zaxxer.hikari.HikariDataSource          # 数据源类型：HikariCP
#    driver-class-name: com.mysql.cj.jdbc.Driver        # 数据库驱动
    url: jdbc:mysql://localhost:3306/imooc-news-dev?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
    username: root
    password: 123456
    hikari:
      connection-timeout: 30000       # 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 默认:30秒
      minimum-idle: 5                 # 最小连接数
      maximum-pool-size: 20           # 最大连接数
      auto-commit: true               # 自动提交
      idle-timeout: 600000            # 连接超时的最大时长（毫秒），超时则被释放（retired），默认:10分钟
      pool-name: DateSourceHikariCP     # 连接池名字
      max-lifetime: 1800000           # 连接的生命时长（毫秒），超时而且没被使用则被释放（retired），默认:30分钟 1800000ms
      connection-test-query: SELECT 1
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

############################################################
#
# mybatis 配置
#
############################################################
mybatis:
  type-aliases-package: com.imooc.pojo          # 所有POJO类所在包路径
  mapper-locations: classpath:mapper/*.xml      # mapper映射文件
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: false  # 驼峰命名
  global-config:
    db-config:
      logic-delete-field: isDelete # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
############################################################
#
# mybatis mapper 配置
#
############################################################
# 通用 Mapper 配置
mapper:
  mappers: com.imooc.my.mapper.MyMapper
  not-empty: false    # 在进行数据库操作的的时候，判断表达式 username != null, 是否追加 username != ''
  identity: MYSQL
# 分页插件配置
pagehelper:
  helperDialect: mysql
  supportMethodsArguments: true
```

# 2-12Swagger2接口文档工具的使用

```
       <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-spring-boot-starter</artifactId>
            <version>2.0.7</version>
        </dependency>
```

# 3-4安装配置整合Redis-1

~~~
tar -zxvf redis...tar
cd redis-..
yum install gcc-c++
make
make install PREFIX=/usr/local/redis
cd redis
cp redis.conf /usr/local/redis

cd /usr/local/redis/bin
vim redis.conf
redis-server redis.conf
ps -ef|grep redis

~~~

3-7联调前端发送短信，解决跨域问题

```

@Configuration
public class CorsConfig {

    public CorsConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        // 1. 添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        // 设置是否发送cookie信息
        config.setAllowCredentials(true);
        // 设置允许请求的方式
        config.addAllowedMethod("*");
        // 设置允许的header
        config.addAllowedHeader("*");
        // 2. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);
        // 3. 返回重新定义好的corsSource
        return new CorsFilter(corsSource);
    }

}
```

3-8拦截并限制60秒用户短信发送

```
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";

    /**
     * 拦截请求，访问controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获得用户ip
        String userIp = IPUtil.getRequestIp(request);
        boolean keyIsExist = redis.keyIsExist(MOBILE_SMSCODE + ":" + userIp);

        if (keyIsExist) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
//            System.out.println("短信发送频率太大！");
            return false;
        }

        /**
         * false：请求被拦截
         * true：请求通过验证，放行
         */
        return true;
    }
    /**
     * 请求访问到controller之后，渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问到controller之后，渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
```

3-9自定义异常，返回错误信息

```
/**
 * 自定义异常
 * 目的：统一处理异常信息
 *      便于解耦，service与controller错误的解耦，不会被service返回的类型而限制
 */
public class MyCustomException extends RuntimeException {

    private ResponseStatusEnum responseStatusEnum;

    public MyCustomException(ResponseStatusEnum responseStatusEnum) {
        super("异常状态码为：" + responseStatusEnum.status()
                + "；具体异常信息为：" + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }

    public ResponseStatusEnum getResponseStatusEnum() {
        return responseStatusEnum;
    }

    public void setResponseStatusEnum(ResponseStatusEnum responseStatusEnum) {
        this.responseStatusEnum = responseStatusEnum;
    }
}




/**
 * 统一异常拦截处理
 * 可以针对异常的类型进行捕获，然后返回json信息到前端
 */
@ControllerAdvice
public class GraceExceptionHandler {

    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyException(MyCustomException e) {
        e.printStackTrace();
        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public GraceJSONResult returnMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_MAX_SIZE_ERROR);
    }

}


/**
 * 优雅的处理异常，统一封装
 */
public class GraceException {

    public static void display(ResponseStatusEnum responseStatusEnum) {
        throw new MyCustomException(responseStatusEnum);
    }

}

```

3-10验证BO信息【注册登录】

```
@Data
public class RegistLoginBo {
    @NotBlank(message = "用户名不能为空")
    private String mobile;
    @NotBlank(message = "密码不能为空")
    private String smsCode;
}


    @Override
    public GraceJSONResult doLogin(RegistLoginBo registLoginBo , BindingResult result) {
        //0.判断BindingResult是否保存错误的验证信息，如果有，则需要返回
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
        }

        String mobile = registLoginBo.getMobile();
        String smsCode = registLoginBo.getSmsCode();
        String redisSMSCode = redisOperator.get(MOBILE_SMSCODE+":"+mobile);
        if(StringUtils.isBlank(mobile) || !redisSMSCode.equals(smsCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        return GraceJSONResult.ok();
    }

```

3-13设置会话与cookie信息【注册登录】

```
    @Override
    public GraceJSONResult doLogin(RegistLoginBo registLoginBo , BindingResult result, HttpServletRequest request,
                                   HttpServletResponse response) throws ParseException {
        //0.判断BindingResult是否保存错误的验证信息，如果有，则需要返回
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
        }

        String mobile = registLoginBo.getMobile();
        String smsCode = registLoginBo.getSmsCode();
        String redisSMSCode = redisOperator.get(MOBILE_SMSCODE+":"+mobile);
        if(StringUtils.isBlank(mobile) || !redisSMSCode.equals(smsCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        AppUser user = appUserService.queryMobileIsExist(mobile);
        if(user != null&& Objects.equals(user.getActive_status(), UserStatus.FROZEN.type)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }else if(user==null){
            user = appUserService.createUser(mobile);
        }
        // 保存用户会话
        if(!Objects.equals(user.getActive_status(), UserStatus.FROZEN.type)){
            //保存token到redis
            String uToken = UUID.randomUUID().toString();
            redisOperator.set("redis_user_token:"+user.getId(),uToken);
            //保存用户id和token到cookie
            setCookie("utoken",uToken,response,COOKIE_MONTH);
            setCookie("uid", user.getId(), response,COOKIE_MONTH);
        }
        //用户登陆后，删除验证码
        redisOperator.del(MOBILE_SMSCODE+":"+mobile);
        return GraceJSONResult.ok(user.getActive_status());
    }
    public void setCookie(String key,String value,HttpServletResponse response,int maxAge){
        try {
            value = URLEncoder.encode(value,"utf-8");
            setCookieValue(key,value,response,maxAge);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void setCookieValue(String key,String value,HttpServletResponse response,int maxAge){

        Cookie cookie = new Cookie(key,value);

        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);

    }
```

3-14资源属性与常量绑定

```
@Value("${website.domain-name}")
private String domainName;
```

3-15 id查询用户账户信息

- 判断用户是否存在
- 查询用户信息
- 返回用户的安全属性

```
@Override
public GraceJSONResult getAccountInfo(String userId) {
    //判断useid不能为空
    if(StringUtils.isBlank(userId)) {
        return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
    }
    AppUser appUser = getUser(userId); //从数据库中查询用户信息
    AppUserVO appUserVO = new AppUserVO();
    BeanUtils.copyProperties(appUser, appUserVO);
    return GraceJSONResult.ok(appUserVO);
}
public AppUser getUser(String userId) {
    return appUserService.getById(userId);
}
```



3-16信息校验【用户资料完善】

```
@NotBlank(message = "用户ID不能为空")
private String id;

@NotBlank(message = "用户昵称不能为空")
@Length(max = 12, message = "用户昵称不能超过12位")
private String nickname;

@NotBlank(message = "用户头像不能为空")
private String face;

@NotBlank(message = "真实姓名不能为空")
private String realname;

@Email
@NotBlank(message = "邮件不能为空")
private String email;

@NotNull(message = "请选择一个性别")
@Min(value = 0, message = "性别选择不正确")
@Max(value = 1, message = "性别选择不正确")
private Integer sex;

@NotNull(message = "请选择生日日期")
@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") // 解决前端日期字符串传到后端后，转换为Date类型
private Date birthday;

@NotBlank(message = "请选择所在城市")
private String province;

@NotBlank(message = "请选择所在城市")
private String city;

@NotBlank(message = "请选择所在城市")
private String district;
```



```
public GraceJSONResult updateUserInfo(UpdateUserInfoBO updateUserInfoBO, BindingResult result) {
    //0.判断BindingResult是否保存错误的验证信息，如果有，则需要返回
    if(result.hasErrors()){
        Map<String, String> errors = getErrors(result);
        log.info("用户信息更新失败，错误信息为：{}", errors);
        return GraceJSONResult.errorMap(errors);
    }
    //1.更新用户信息
    AppUser appUser = new AppUser();
    BeanUtils.copyProperties(updateUserInfoBO, appUser);
    boolean res = appUserService.updateById(appUser);
    if(res) {
        return GraceJSONResult.ok();
    } else {
        return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_UPDATE_ERROR);
    }
}
```
