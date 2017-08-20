package me.ucake.session.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public class SimpleSessionRequest extends HttpServletRequestWrapper {
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public SimpleSessionRequest(HttpServletRequest request) {
        super(request);
    }
}
