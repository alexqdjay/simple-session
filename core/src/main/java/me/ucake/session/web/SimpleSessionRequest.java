package me.ucake.session.web;

import me.ucake.session.Consts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public class SimpleSessionRequest extends HttpServletRequestWrapper {

    private SessionRepository sessionRepository;
    private SessionTransaction sessionTransaction;


    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public SimpleSessionRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public HttpSession getSession(boolean create) {
        Session session = getCurrentSession();

        if (session != null) {
            return session;
        }

        String sessionId = getRequestedSessionId();

        if (sessionId != null) {
            session = sessionRepository.getSessionById(sessionId);
            if (session != null) {
                return session;
            }
        }

        if (!create) {
            return null;
        }

        session = sessionRepository.createSession(getServletContext());

        return session;
    }

    @Override
    public HttpSession getSession() {
        return this.getSession(false);
    }

    @Override
    public String changeSessionId() {
        return super.changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return super.isRequestedSessionIdValid();
    }

    @Override
    public String getRequestedSessionId() {
        return sessionTransaction.getRequestedSessionId(this);
    }


    public Session getCurrentSession() {
        Object tmp = this.getAttribute(Consts.RequestAttributes.ATTR_CURRENT_SESSION);
        if (tmp == null) {
            return null;
        }
        return (Session)tmp;
    }

    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void setSessionTransaction(SessionTransaction sessionTransaction) {
        this.sessionTransaction = sessionTransaction;
    }
}
