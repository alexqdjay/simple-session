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
            servletRequest.setAttribute(ALREADY_VISITED_NAME, Boolean.TRUE);
            chain.doFilter(new SimpleSessionRequest(servletRequest),
                    new SimpleSessionResponse(servletResponse));
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