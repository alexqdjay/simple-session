package me.ucake.session.redis;

import me.ucake.session.Consts;
import me.ucake.session.FlushMode;
import me.ucake.session.web.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class RedisSessionRepository implements SessionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(RedisSessionRepository.class);

    private RedisTemplate redisTemplate;
    private FlushMode flushMode;
    private int maxExpireSecond = 1800; // 默认过期时间30min

    public RedisSessionRepository(RedisTemplate redisTemplate) {
        this(redisTemplate, FlushMode.LAZY);
    }

    public RedisSessionRepository(RedisTemplate redisTemplate, FlushMode flushMode) {
        this(redisTemplate, flushMode, 1800);
    }

    public RedisSessionRepository(RedisTemplate redisTemplate, FlushMode flushMode, int maxExpireSecond) {
        this.redisTemplate = redisTemplate;
        this.flushMode = flushMode;
        this.maxExpireSecond = maxExpireSecond;
    }

    @Override
    public Map<String, Object> getSessionAttributesById(String sessionId) {
        try {
            return redisTemplate.hmget(sessionIdKey(sessionId));
        } catch (Exception e) {
            LOG.error("get session attributes error msg:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public void saveAttributes(String sessionId, Map<String, Object> attributes) {
        redisTemplate.hmset(sessionIdKey(sessionId), attributes, maxExpireSecond);
    }

    private String sessionIdKey(String sessionId) {
        return String.format(Consts.RedisFields.FIELDS_PREFIX, sessionId);
    }

    @Override
    public FlushMode getFlushMode() {
        return flushMode;
    }

    @Override
    public void removeSession(String sessionId) {
        redisTemplate.delete(sessionIdKey(sessionId));
    }
}
