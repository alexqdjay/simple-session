package me.ucake.session.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static me.ucake.session.Consts.RequestAttributes.*;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public class SimpleSessionRequest extends HttpServletRequestWrapper {

    private SessionRepository sessionRepository;
    private SessionTransaction sessionTransaction;
    private HttpServletResponse response;

    private Boolean requestedSessionIdValid;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @param sessionRepository
     * @param sessionTransaction
     */
    public SimpleSessionRequest(HttpServletRequest request,
                                HttpServletResponse response,
                                SessionRepository sessionRepository,
                                SessionTransaction sessionTransaction) {
        super(request);
        this.response = response;
        this.sessionRepository = sessionRepository;
        this.sessionTransaction = sessionTransaction;
    }

    @Override
    public HttpSession getSession(boolean create) {
        Session session = getCurrentSession();

        if (session != null) {
            return session;
        }

        String sessionId = getRequestedSessionId();

        if (sessionId != null) {
            session = getSessionById(sessionId);
            if (session != null) {
                this.requestedSessionIdValid = true;
                setCurrentSession(session);
                return session;
            } else {
                //TODO mark session invalid
            }
        }

        if (!create) {
            return null;
        }

        session = Session.createNew(getServletContext(), sessionRepository);

        this.setCurrentSession(session);

        return session;
    }

    private Session getSessionById(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return Session.restoreById(sessionId, getServletContext(), sessionRepository);
    }

    @Override
    public HttpSession getSession() {
        return this.getSession(false);
    }

    @Override
    public String changeSessionId() {
        HttpSession session = getSession(false);
        if (session == null) {
            throw new IllegalStateException(
                    "Cannot change session ID. There is no session associated with this request.");
        }

        return getCurrentSession().changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        if (requestedSessionIdValid != null) {
            return this.requestedSessionIdValid.booleanValue();
        }

        String sessionId = getRequestedSessionId();
        Session session = getSessionById(sessionId);

        this.requestedSessionIdValid = session != null;

        return this.requestedSessionIdValid;
    }

    @Override
    public String getRequestedSessionId() {
        return sessionTransaction.getRequestedSessionId(this);
    }

    protected void commitSession() {
        Session session = getCurrentSession();
        if (session != null) {
            if (!session.getId().equals(getRequestedSessionId())) {
                this.sessionTransaction.onNewSession(session, this, this.response);
            }
        }
    }

    private Session getCurrentSession() {
        Object tmp = this.getAttribute(ATTR_CURRENT_SESSION);
        if (tmp == null) {
            return null;
        }
        return (Session)tmp;
    }

    public void setCurrentSession(Session session) {
        if (session == null) {
            this.removeAttribute(ATTR_CURRENT_SESSION);
            return;
        }
        this.setAttribute(ATTR_CURRENT_SESSION, session);
    }

    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void setSessionTransaction(SessionTransaction sessionTransaction) {
        this.sessionTransaction = sessionTransaction;
    }
}
