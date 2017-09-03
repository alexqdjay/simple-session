package me.ucake.session.redis;

import me.ucake.session.Consts;
import me.ucake.session.FlushMode;
import me.ucake.session.web.Session;
import me.ucake.session.web.SessionRepository;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class RedisSessionRepository implements SessionRepository {

    private RedisTemplate redisTemplate;
    private FlushMode flushMode;

    @Override
    public Session getSessionById(String sessionId) {
        return null;
    }

    @Override
    public Session createSession(ServletContext servletContext) {
        Session newSession = Session.createNew(servletContext, flushMode);
        newSession.setSessionRepository(this);
        return newSession;
    }

    @Override
    public void saveAttributes(String sessionId, Map<String, Object> attributes) {
        redisTemplate.hmset(sessionIdKey(sessionId), attributes);
    }

    private String sessionIdKey(String sessionId) {
        return String.format(Consts.RedisFields.FIELDS_PREFIX, sessionId);
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
    }
}
