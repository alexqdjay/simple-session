package me.ucake.session.web;

import me.ucake.session.core.LogHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public class SimpleSessionFilter implements Filter {

    private static final String ALREADY_VISITED_NAME =
            SimpleSessionFilter.class.getName().concat(".VISITED");

    private static final SessionTransaction DEFAULT_SESSION_TRANSACTION = new CookieBasedTransaction();

    private SessionRepository sessionRepository;
    private SessionTransaction sessionTransaction = DEFAULT_SESSION_TRANSACTION;

    public SimpleSessionFilter(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public SessionTransaction getSessionTransaction() {
        return sessionTransaction;
    }

    public void setSessionTransaction(SessionTransaction sessionTransaction) {
        this.sessionTransaction = sessionTransaction;
    }

    public SessionRepository getSessionRepository() {
        return sessionRepository;
    }

    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("the request is not HttpServletRequest");
        }

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        // 加入访问标识, 以防多次访问
        boolean visited = servletRequest.getAttribute(ALREADY_VISITED_NAME) != null;

        if (visited) {
            chain.doFilter(request, response);
        } else {
            try {
                doFilter(chain, servletRequest, servletResponse);
            } finally {
                servletRequest.removeAttribute(ALREADY_VISITED_NAME);
            }
        }
    }

    private void doFilter(FilterChain chain, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws IOException, ServletException {
        SimpleSessionRequest sessionRequest = new SimpleSessionRequest(servletRequest,
                servletResponse,
                sessionRepository,
                getSessionTransaction());
        SimpleSessionResponse sessionResponse = new SimpleSessionResponse(servletResponse, sessionRepository);
        servletRequest.setAttribute(ALREADY_VISITED_NAME, Boolean.TRUE);
        try {
            chain.doFilter(sessionRequest, sessionResponse);
        } finally {
            sessionRequest.commitSession();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
        LogHolder.getLogger().info(SimpleSessionFilter.class.getSimpleName().concat(" destroy"));
    }
}
