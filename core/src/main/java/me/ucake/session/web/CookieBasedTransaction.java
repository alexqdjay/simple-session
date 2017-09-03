package me.ucake.session.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class CookieBasedTransaction implements SessionTransaction {

    private static final String COOKIE_NAME_SESSION = "ssession";

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
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
}
