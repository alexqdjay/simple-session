package me.ucake.session.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by alexqdjay on 2017/9/2.
 */
public interface SessionStrategy {

    String getRequestedSessionId(HttpServletRequest request);

    void onNewSession(HttpSession session, HttpServletRequest request, HttpServletResponse response);

    void onInvalidateSession(HttpServletRequest request, HttpServletResponse response);

}
