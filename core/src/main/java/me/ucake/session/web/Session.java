package me.ucake.session.web;


import me.ucake.session.FlushMode;
import me.ucake.session.core.UUIDGen;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.*;

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
    private boolean invalidated = false;

    private ServletContext servletContext;

    private SessionRepository sessionRepository;

    public static Session createNew(ServletContext servletContext,
                                    SessionRepository sessionRepository) {
        Session session = new Session();
        session.id = UUIDGen.gen();
        session.servletContext = servletContext;
        session.createTime = session.lastAccessTime = System.currentTimeMillis();
        session.isNew = true;
        session.cached.put(FIELD_CREATE_TIME_NAME, session.createTime);
        session.cached.put(FIELD_LAST_ACCESS_TIME_NAME, session.lastAccessTime);
        session.cached.put(FIELD_MAX_INACTIVE_INTERVAL_NAME, session.maxInactiveInterval);
        session.setSessionRepository(sessionRepository);
        session.flushToRepository();
        return session;
    }

    public static Session restoreById(String sessionId,
                                      ServletContext servletContext,
                                      SessionRepository sessionRepository) {
        Map<String, Object> attributes = sessionRepository.getSessionAttributesById(sessionId);
        if (attributes == null) {
            return null;
        }
        Session session = new Session();
        session.id = sessionId;
        session.lastAccessTime = System.currentTimeMillis();
        session.cached.put(FIELD_LAST_ACCESS_TIME_NAME, session.lastAccessTime);
        session.servletContext = servletContext;
        session.attributes.putAll(attributes);
        session.fillField();
        session.setSessionRepository(sessionRepository);
        session.flushToRepository();
        return session;
    }

    private void fillField() {
        if (attributes.containsKey(FIELD_CREATE_TIME_NAME)) {
            this.createTime = (long) attributes.get(FIELD_CREATE_TIME_NAME);
        }

        if (attributes.containsKey(FIELD_MAX_INACTIVE_INTERVAL_NAME)) {
            this.maxInactiveInterval = (int) attributes.get(FIELD_MAX_INACTIVE_INTERVAL_NAME);
        }
    }

    @Override
    public long getCreationTime() {
        checkState();
        return this.createTime;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public long getLastAccessedTime() {
        checkState();
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
        checkState();
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        checkState();
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkState();
        Iterator<String> namesItor = attributes.keySet().iterator();
        Enumeration<String> enumeration = new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return namesItor.hasNext();
            }

            @Override
            public String nextElement() {
                return namesItor.next();
            }
        };
        return enumeration;
    }

    @Override
    public String[] getValueNames() {
        checkState();
        Set<String> names = attributes.keySet();
        return names.toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkState();
        this.attributes.put(name, value);
        this.putAndFlush(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.setAttribute(name, null);
    }

    @Override
    public void removeValue(String name) {
        this.removeAttribute(name);
    }

    @Override
    public void invalidate() {
        checkState();
        this.invalidated = true;
    }

    @Override
    public boolean isNew() {
        checkState();
        return this.isNew;
    }

    protected String changeSessionId() {
        this.id = UUIDGen.gen();
        return this.id;
    }

    public void hasAccessed() {
        checkState();
        this.lastAccessTime = System.currentTimeMillis();
    }

    private void flushToRepository() {
        if (this.getFlushMode() == FlushMode.LAZY) {
            return;
        }
        this.sessionRepository.saveAttributes(this.id, this.cached);
    }

    private void putAndFlush(String name, Object value) {
        this.cached.put(name, value);
        this.flushToRepository();
    }

    private void checkState() {
        if (this.invalidated) {
            throw new IllegalStateException("The Session is invalidated!");
        }
    }

    public FlushMode getFlushMode() {
        return sessionRepository.getFlushMode();
    }

    private void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
}
