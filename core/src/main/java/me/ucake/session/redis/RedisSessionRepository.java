package me.ucake.session.redis;

import me.ucake.session.web.Session;
import me.ucake.session.web.SessionRepository;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class RedisSessionRepository implements SessionRepository {

    private RedisTemplate redisTemplate;

    @Override
    public Session getSessionById(String sessionId) {
        return null;
    }

    @Override
    public Session createSession(ServletContext servletContext) {
        return null;
    }

    @Override
    public void saveAttributes(String sessionId, Map<String, Object> attributes) {
        redisTemplate.hmset(sessionId, attributes);
    }
}
