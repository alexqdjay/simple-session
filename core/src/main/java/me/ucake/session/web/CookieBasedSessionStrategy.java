package me.ucake.session.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class CookieBasedSessionStrategy implements SessionStrategy {

    public static final String COOKIE_NAME_SESSION = "ssession";

    private Boolean useSecure = Boolean.TRUE;
    private boolean useHttpOnlyCookie = true;
    private String cookiePath;

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie == null) {
                continue;
            }

            if (!COOKIE_NAME_SESSION.equalsIgnoreCase(cookie.getName())) {
                continue;
            }

            return cookie.getValue();
        }
        return null;
    }

    @Override
    public void onNewSession(HttpSession session,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        String sessionId = session.getId();
        Cookie cookie = new Cookie(COOKIE_NAME_SESSION, sessionId);
        cookie.setSecure(this.isSecureCookie(request));
        cookie.setHttpOnly(this.useHttpOnlyCookie);
        cookie.setPath(getPath(request));
        response.addCookie(cookie);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME_SESSION, request.getRequestedSessionId());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getPath(HttpServletRequest request) {
        if (this.cookiePath == null) {
            return request.getContextPath() + "/";
        }
        return this.cookiePath;
    }

    private boolean isSecureCookie(HttpServletRequest request) {
        if (this.useSecure == null) {
            return request.isSecure();
        }
        return useSecure;
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public void setUseHttpOnlyCookie(boolean useHttpOnlyCookie) {
        this.useHttpOnlyCookie = useHttpOnlyCookie;
    }

    public void setUseSecure(boolean useSecure) {
        this.useSecure = useSecure;
    }
}
