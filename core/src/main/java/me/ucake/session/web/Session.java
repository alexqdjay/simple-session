package me.ucake.session.web;


import me.ucake.session.FlushMode;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static me.ucake.session.Consts.RedisFields.*;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public class Session implements Serializable, HttpSession {

    private static final long serialVersionUID = 4200021293916883369L;
    private static final int DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS = 30 * 60;
    private static final HttpSessionContext EMPTY_SESSION_CONTEXT = new HttpSessionContext() {
        private final Enumeration<String> EMPTY_IDS = new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public String nextElement() {
                throw new NoSuchElementException();
            }
        };

        @Override
        public HttpSession getSession(String sessionId) {
            return null;
        }

        @Override
        public Enumeration<String> getIds() {
            return EMPTY_IDS;
        }
    };

    private String id;
    private String originalId;
    private long createTime;
    private long lastAccessTime;
    private int maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;
    private Map<String, Object> attributes = new HashMap<>();
    private Map<String, Object> cached = new HashMap<>();
    private boolean isNew = false;
    private FlushMode flushMode;

    private ServletContext servletContext;

    private SessionRepository sessionRepository;

    public static Session createNew(ServletContext servletContext, FlushMode flushMode) {
        Session session = new Session(flushMode);
        session.servletContext = servletContext;
        session.createTime = session.lastAccessTime = System.currentTimeMillis();
        session.isNew = true;
        session.cached.put(FIELD_CREATE_TIME_NAME, session.createTime);
        session.cached.put(FIELD_LAST_ACCESS_TIME_NAME, session.lastAccessTime);
        session.cached.put(FIELD_MAX_INACTIVE_INTERVAL_NAME, session.maxInactiveInterval);
        return session;
    }

    public Session(FlushMode flushMode) {
        this.flushMode = flushMode;
    }

    @Override
    public long getCreationTime() {
        return this.createTime;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessTime;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return EMPTY_SESSION_CONTEXT;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    @Override
    public void putValue(String name, Object value) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void hasAccessed() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    public void updateToRepository() {
        if (this.flushMode == FlushMode.LAZY) {
            return;
        }
        for (Map.Entry<String, Object> entry : cached.entrySet()) {

        }
    }

    public FlushMode getFlushMode() {
        return flushMode;
    }

}
