package me.ucake.session.redis;

import me.ucake.session.Consts;
import me.ucake.session.FlushMode;
import me.ucake.session.web.Session;
import me.ucake.session.web.SessionRepository;

import java.util.Map;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class RedisSessionRepository implements SessionRepository {

    private RedisTemplate redisTemplate;
    private FlushMode flushMode;

    public RedisSessionRepository(RedisTemplate redisTemplate, FlushMode flushMode) {
        this.redisTemplate = redisTemplate;
        this.flushMode = flushMode;
    }

    @Override
    public Map<String, Object> getSessionAttributesById(String sessionId) {
        //TODO 补充getSessionById 实现
        return null;
    }

    @Override
    public void saveAttributes(String sessionId, Map<String, Object> attributes) {
        redisTemplate.hmset(sessionIdKey(sessionId), attributes);
    }

    private String sessionIdKey(String sessionId) {
        return String.format(Consts.RedisFields.FIELDS_PREFIX, sessionId);
    }

    @Override
    public FlushMode getFlushMode() {
        return flushMode;
    }
}
