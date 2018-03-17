# Simple Session
> A Distributed Session Frame (Java)  
一个简化`SpringSession`的分布式Session框架, 基于Java, 旨在简单好用

## Quick Start

如果你的项目是`SpringBoot`那么看下面`1. Spring boot`  
如果你的项目是`SpringMVC`项目看下面`2. SpringMVC`

### 1. Spring boot

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

### 2. SpringMVC

SpringMVC 只需要配置一个`SimpleSessionFactory`, 当然首先需要配置`JedisPool`(省略)

```xml
<bean class="me.ucake.session.config.xml.SimpleSessionFactory">
	<!-- jedisPool 实例 -->
	<property name="jedisPool" ref="jedisPool"></property>
	<!-- 保存策略: IMMEDIATE, LAZY -->
	<property name="flushMode" ref="LAZY"></property>
	<!-- session传递方式: cookie, header -->
	<property name="strategy" ref="cookie"></property>
</bean>

```



