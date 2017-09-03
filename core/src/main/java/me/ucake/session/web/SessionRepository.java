package me.ucake.session.web;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public interface SessionRepository {

    Session getSessionById(String sessionId);

    Session createSession(ServletContext servletContext);

    void saveAttributes(String sessionId, Map<String, Object> attributes);
}
