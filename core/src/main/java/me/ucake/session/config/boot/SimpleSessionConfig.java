package me.ucake.session.config.boot;

import me.ucake.session.redis.RedisSessionRepository;
import me.ucake.session.redis.RedisTemplate;
import me.ucake.session.web.*;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * Created by alexqdjay on 2018/3/17.
 */
@Configuration
@EnableConfigurationProperties(SimpleSessionProperties.class)
public class SimpleSessionConfig {

    @Autowired
    private SimpleSessionProperties simpleSessionProperties;

    @Bean
    @ConditionalOnMissingBean(JedisPool.class)
    public JedisPool jedisPool() {
        SimpleSessionProperties.Redis redis = simpleSessionProperties.getRedis();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(redis.getMaxIdle());
        poolConfig.setMinIdle(redis.getMinIdle());
        poolConfig.setMaxTotal(redis.getMaxActive());
        poolConfig.setMaxWaitMillis(redis.getMaxWait());
        poolConfig.setTestWhileIdle(redis.isTestWhileIdle());
        poolConfig.setTimeBetweenEvictionRunsMillis(redis.getTimeBetweenEvictionRunsMillis());
        poolConfig.setBlockWhenExhausted(redis.isBlockWhenExhausted());
        poolConfig.setMinEvictableIdleTimeMillis(redis.getMinEvictableIdleTimeMillis());
        poolConfig.setNumTestsPerEvictionRun(redis.getNumTestsPerEvictionRun());

        if (redis.getPassword() == null) {
            return new JedisPool(poolConfig, redis.getHost(),
                redis.getPort(), redis.getTimeout());
        } else {
            return new JedisPool(poolConfig, redis.getHost(),
                    redis.getPort(), redis.getTimeout(), redis.getPassword());
        }
    }

    @Bean
    @ConditionalOnMissingBean(SessionRepository.class)
    public RedisSessionRepository redisSessionRepository(JedisPool jedisPool) {
        return new RedisSessionRepository(new RedisTemplate(jedisPool),
                simpleSessionProperties.getFlushMode());
    }

    private SessionStrategy sessionStrategy() {
        return SessionStrategy.valueOf(simpleSessionProperties.getStrategy());
    }

    private SimpleSessionFilter simpleSessionFilter(SessionRepository sessionRepository) {
        SimpleSessionFilter simpleSessionFilter =  new SimpleSessionFilter(sessionRepository);
        simpleSessionFilter.setSessionStrategy(sessionStrategy());
        return simpleSessionFilter;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(SessionRepository sessionRepository) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(simpleSessionFilter(sessionRepository));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("simpleSessionFilter");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

}
