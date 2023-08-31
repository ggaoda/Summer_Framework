# Summer_Framework
Spring的简易实现.

![logo](logo.png)



Summer Framework 是一个迷你版的 Spring Framework.

Components of Summer Framework:

- summer-context: 支持基于注解注入的核心 IoC 容器.
- summer-aop: 基于注解的代理AOP
- summer-jdbc: 提供 JdbcTemplate 和 声明式事务管理.
- summer-web: 基于 Servlet 6.0 的 Web 支持.
- summer-boot: 像 Spring Boot 一样简单快速地运行应用程序.

## 说明

  本框架是根据廖雪峰老师的教程[<手写Spring>](https://www.liaoxuefeng.com/wiki/1539348902182944)所写.

  其中补充了个人代码注释,并进行了一些测试,仅学习使用.


## 快速使用

  本框架基于 JDK18 Servlet6.0 . 依赖部分详见pom.xml
  因本框架未曾进行整合,测试用例等仍旧保留,若要学习测试,请下载后直接在src下编写测试代码