package me.ucake.session.web;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by alexqdjay on 2017/9/2.
 */
public interface SessionTransaction {

    String getRequestedSessionId(HttpServletRequest request);

}
