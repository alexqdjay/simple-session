package me.ucake.session.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by alexqdjay on 2017/8/20.
 */
public class SimpleSessionResponse extends HttpServletResponseWrapper {

    private SimpleSessionRequest request;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public SimpleSessionResponse(HttpServletResponse response, SimpleSessionRequest request) {
        super(response);
        this.request = request;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        doOnResponseCommitted();
        super.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        doOnResponseCommitted();
        super.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        doOnResponseCommitted();
        super.sendRedirect(location);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        doOnResponseCommitted();
        return super.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        doOnResponseCommitted();
        return super.getWriter();
    }

    @Override
    public void flushBuffer() throws IOException {
        doOnResponseCommitted();
        super.flushBuffer();
    }

    private void doOnResponseCommitted() {
        doOnResponseCommitted();
        this.request.commitSession();
    }



}
