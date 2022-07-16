# mall
    项目简介：一个分布式前后端分离的电商项目，采用了当今主流的系统架构和技术栈
    技术栈：
    Spring Boot、SpringCloudAlibaba、Mybatis-Plus、MySQL、Redis、
    ElasticSearch、Nginx、Swagger、RabbitMQ、Nginx、Vue、themlef
    项目描述：
    1、手机商城后端分为商城后台管理系统和商城本体，共由13个微服务组成,后台系统可以实现品牌管理,商品属性管理,商品发布、上架、下架，商品库存管理等功能。
    2、商城本体可以实现商品搜索、商品信息展示、购物车、订单结算等功能。
    3、使用nacos作为注册中心与配置中心,可以感知各个微服务的位置,同时将各个配置上传至网上,实现源码与配置的分离。
    4、借助nginx负载均衡到Gateway网关,搭建本地域名访问环境，并实现动静资源分离。
    5、微服务之间使用OpenFegin进行远程调用，添加RequestInterceptor解决请求头中Cookie丢失问题
    6、使用Redis作为缓存中间件，缓存频繁访问的数据（购物车数据，首页数据等）提升系统性能。使用Redisson实现分布式锁+过期时间的方案解决缓存一致性问题，使用Spring Cache注解简化开发。
    7、使用Spring Session + Redis解决不同子域名下的Session共享问题
    8、使用Seata解决分布式事务问题,对于高并发业务,使用RabbitMQ做可靠消息保证最终事务一致性（开启确认机制保障消息可靠抵达，通过业务设计解决消息重复消费问题）。
    9、每个微服务整合Swagger3,方便接口进行测试和生成接口文档。
    10、项目中图片上传使用阿里云对象存储技术,所有图片存储在阿里云创建的bucket中。
    11、商品检索使用全文索引技术ElasticSearch。
    12、实现了Oauth2微信社交登录,QQ邮箱验证码等功能,使用BCryptPasswordEncoder（SHA-256 Hash算法+随机盐值）加密用户密码。
    13、实现了支付宝沙箱支付功能。
    14、使用Sleuth+Zipkin实现接口调用链路追踪，辅助排错，接口调优
    15、其他：
        使用统一异常处理返回异常/成功状态码
        使用Validator统一参数校验
        使用CompletableFuture+线程池进行异步编程提高系统响应速度
        使用MyBatis-Plus逆向生成基础业务代码，快速初始化项目减少工作量。
        使用雪花算法生成订单编号
        主要职责：全栈开发
##auth模块
    权鉴系统
##cart模块
    购物车
##common模块
    公共代码
##coupon模块
    优惠系统
##gateway模块
    网关
##member模块
    会员系统
##order模块
    订单系统
##product模块
    商品系统
##rabbitMq模块
    RabbitMq服务
##search模块
    全局检索ElasticSearch系统
##third-party模块
    三方交互系统
##ware模块
    仓库系统
##renren-fast模块
    后台管理系统
##renren-generator模块
    大大减少开发工作量 
    根据数据库内容逆向生成增删改查业务代码
    同时也可生成后端管理界面的vue代码
    可在resources--template目录下自定义生成的内容