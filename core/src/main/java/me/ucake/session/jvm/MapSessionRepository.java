package me.ucake.session.jvm;

import me.ucake.session.FlushMode;
import me.ucake.session.web.Session;
import me.ucake.session.web.SessionRepository;

import javax.servlet.ServletContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alexqdjay on 2018/2/23.
 */
public class MapSessionRepository implements SessionRepository {

    private final Map<String, Map<String, Object>> sessionValuesMap = new ConcurrentHashMap<>();

    private FlushMode flushMode;

    public MapSessionRepository(FlushMode flushMode) {
        this.flushMode = flushMode;
    }

    @Override
    public Session getSessionById(String sessionId) {
        throw new IllegalStateException();
    }

    @Override
    public Session createSession(ServletContext servletContext) {
        return Session.createNew(servletContext, flushMode, this);
    }

    @Override
    public void saveAttributes(String sessionId, Map<String, Object> attributes) {
        getSessionValues(sessionId).putAll(attributes);
    }

    private Map<String, Object> getSessionValues(String sid) {
        Map<String, Object> sessionValues = sessionValuesMap.get(sid);
        if (sessionValues == null) {
            sessionValues = new ConcurrentHashMap<>();
            Map<String, Object> preMap = sessionValuesMap.putIfAbsent(sid, sessionValues);
            if (preMap != null) {
                sessionValues = preMap;
            }
        }
        return sessionValues;
    }
}
