package me.ucake.session.web;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public class SimpleSessionResponse extends HttpServletResponseWrapper {

    SessionRepository sessionRepository;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public SimpleSessionResponse(HttpServletResponse response) {
        super(response);
    }
}
