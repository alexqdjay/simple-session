# Simple Session
> A Distributed Session Frame (Java)  
一个简化`SpringSession`的分布式Session框架, 基于Java, 旨在简单好用

## 1. Quick Start

如果你的项目是`SpringBoot`那么看下面`1. Spring boot`  
如果你的项目是`SpringMVC`项目看下面`2. SpringMVC`

### 1.1 Spring boot

**Step1.** Add `@EnableSimpleSession` on your Main Class


```Java
@SpringBootApplication
@EnableSimpleSession
public class MainRun {
	...
}
```

**Step2.** Config your Redis

如果已经配置了一个`JedisPool`实例，那么可以跳过这步；没有的话，需要配置`application.properties `

```bash
// application.properties
simplesession.redis.host=127.0.0.1
simplesession.redis.port=6379
```

### 1.2 SpringMVC

SpringMVC 只需要配置一个`SimpleSessionFactory`, 当然首先需要配置`JedisPool`(省略)

```xml
// application-context.xml
<bean id="simpleSessionFilter" class="me.ucake.session.config.xml.SimpleSessionFactory">
	<!-- jedisPool 实例 -->
	<property name="jedisPool" ref="jedisPool"></property>
	<!-- 保存策略: IMMEDIATE, LAZY -->
	<property name="flushMode" ref="LAZY"></property>
	<!-- session传递方式: cookie, header -->
	<property name="strategy" ref="cookie"></property>
</bean>


// web.xml
<filter>
    <filter-name>DelegatingFilterProxy</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    <init-param>
        <param-name>targetBeanName</param-name>
        <param-value>simpleSessionFilter</param-value>         
    </init-param>
    <init-param>
        <param-name>targetFilterLifecycle</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>DelegatingFilterProxy</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

## 2. 使用

`Simple Session` 使用跟普通的session一致，通过`request.getSession()`可以获取。

**限制：**`Simple Session`不允许在`commit`以后再进行操作session，简单说就是在`Filter`中尽量不要进行操作`session`。取消了`Session`的**过期、创建等Context事件**。

`Simple Session`使用`Redis`作为存储，可以配置保存策略：IMMEDIATE(立即)、LAZY(懒保存)，当设置为 *IMMEDIATE* 时属性修改等操作都会马上同步到`Redis`，当设置为 *LAZY* 时会延迟操，等待请求完成后一并保存到`Redis`。

至此基本的使用都已经可以了，其他的**详细使用开发手册见后续放出的文档。**



