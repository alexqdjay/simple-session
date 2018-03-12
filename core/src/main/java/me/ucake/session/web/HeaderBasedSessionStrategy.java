package me.ucake.session.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by alexqdjay on 2018/3/12.
 */
public class HeaderBasedSessionStrategy implements SessionStrategy {

    private static final String HEADER_TOKEN = "x-auth-token";

    private String tokenHeaderName = HEADER_TOKEN;

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        return request.getHeader(tokenHeaderName);
    }

    @Override
    public void onNewSession(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(tokenHeaderName, session.getId());
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(tokenHeaderName, "");
    }

    public void setTokenHeaderName(String tokenHeaderName) {
        if (tokenHeaderName == null || tokenHeaderName.trim().isEmpty()) {
            throw new IllegalArgumentException("can not be empty or null");
        }
        this.tokenHeaderName = tokenHeaderName;
    }
}
