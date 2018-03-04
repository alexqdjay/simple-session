package me.ucake.session.jvm;

import me.ucake.session.FlushMode;
import me.ucake.session.web.Session;
import me.ucake.session.web.SessionRepository;

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

    public MapSessionRepository() {
        this(FlushMode.IMMEDIATE);
    }

    @Override
    public Map<String, Object> getSessionAttributesById(String sessionId) {
        return sessionValuesMap.get(sessionId);
    }

    @Override
    public void saveAttributes(String sessionId, Map<String, Object> attributes) {
        if (attributes == null) {
            return;
        }
        Map<String, Object> values = getSessionValues(sessionId);
        attributes.entrySet().stream().forEach(entry -> {
            if (entry.getValue() == null) {
                values.remove(entry.getKey());
            } else {
                values.put(entry.getKey(), entry.getValue());
            }
        });
    }

    @Override
    public FlushMode getFlushMode() {
        return flushMode;
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
