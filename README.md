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

~~~

