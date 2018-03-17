package me.ucake.session.config.xml;

import me.ucake.session.FlushMode;
import me.ucake.session.redis.RedisSessionRepository;
import me.ucake.session.redis.RedisTemplate;
import me.ucake.session.web.SessionRepository;
import me.ucake.session.web.SessionStrategy;
import me.ucake.session.web.SimpleSessionFilter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.JedisPool;

/**
 * Created by alexqdjay on 2018/3/17.
 */
public class SimpleSessionFactory implements FactoryBean<SimpleSessionFilter>, InitializingBean {

    private JedisPool jedisPool;

    private String strategy;

    private SessionRepository sessionRepository;

    @Override
    public SimpleSessionFilter getObject() throws Exception {
        SimpleSessionFilter simpleSessionFilter = new SimpleSessionFilter(sessionRepository);
        simpleSessionFilter.setSessionStrategy(SessionStrategy.valueOf(strategy));
        return simpleSessionFilter;
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleSessionFilter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setStrategy(String sessionStrategy) {
        this.strategy = sessionStrategy;
    }

    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (sessionRepository == null) {
            if (jedisPool == null) {
                throw new IllegalArgumentException("jedisPool must not be null");
            }
            FlushMode flushMode = FlushMode.valueOf(strategy);
            sessionRepository = new RedisSessionRepository(new RedisTemplate(jedisPool), flushMode);
        }
    }
}
