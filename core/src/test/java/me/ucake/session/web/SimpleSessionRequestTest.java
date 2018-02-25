package me.ucake.session.web;

import me.ucake.session.jvm.MapSessionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexqdjay on 2018/2/25.
 */
public class SimpleSessionRequestTest {

    private SimpleSessionFilter simpleSessionFilter;
    private SessionRepository sessionRepository;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private SessionTransaction mockSessionTransaction;
    private SimpleSessionRequest request;


    @Before
    public void setup() {
        setupRequest();

        mockSessionTransaction = mock(SessionTransaction.class);

        sessionRepository = new MapSessionRepository();

        simpleSessionFilter = new SimpleSessionFilter(sessionRepository);
    }

    @Test
    public void test_getSession() throws IOException, ServletException {
        doInFilter((request, response) -> {
            // 初始化, 没有session
            HttpSession session = request.getSession(false);
            assertThat(session).isNull();

            // create = true, session not null
            session = request.getSession(true);
            assertThat(session).isNotNull();
        });

        nextRequest();

        doInFilter((request, response) -> {
            HttpSession session = request.getSession(false);
            assertThat(session).isNotNull();
        });
    }

    @Test
    public void test_createDate() throws IOException, ServletException, InterruptedException {
        String ATTR_CREATE = "ATTR_CREATE";
        doInFilter((request, response) -> {
            HttpSession session = request.getSession(true);
            long now = System.currentTimeMillis();
            assertThat(now - session.getCreationTime()).isLessThan(100);

            request.setAttribute(ATTR_CREATE, session.getCreationTime());
        });

        long createTime = (long) mockRequest.getAttribute(ATTR_CREATE);
        TimeUnit.MILLISECONDS.sleep(10);
        nextRequest();

        doInFilter((request, response) -> {
            HttpSession session = request.getSession();
            assertThat(session.getCreationTime()).isEqualTo(createTime);
        });
    }

    @Test
    public void test_lastAccessTime() throws IOException, ServletException, InterruptedException {
        String ATTR_LAST = "ATTR_LAST";
        doInFilter((request, response) -> {
            HttpSession session = request.getSession(true);
            long now = System.currentTimeMillis();
            assertThat(now - session.getLastAccessedTime()).isLessThan(100);

            request.setAttribute(ATTR_LAST, session.getLastAccessedTime());
        });

        long last = (long) mockRequest.getAttribute(ATTR_LAST);
        TimeUnit.MILLISECONDS.sleep(10);
        nextRequest();

        doInFilter((request, response) -> {
            HttpSession session = request.getSession();
            assertThat(session.getLastAccessedTime()).isGreaterThan(last);

            long lastAccessedTime = session.getLastAccessedTime();
            request.getSession();
            assertThat(session.getLastAccessedTime()).isEqualTo(lastAccessedTime);
        });
    }

    @Test
    public void test_getSessionByCurrent() throws IOException, ServletException {
        doInFilter((request, response) -> {
            HttpSession session = request.getSession(true);

            session = request.getSession();
            assertThat(session).isNotNull();
        });
    }

    @Test(expected = IllegalStateException.class)
    public void test_changeSessionId_exception() throws IOException, ServletException {
        doInFilter((request, response) -> {
            request.changeSessionId();
        });
    }

    @Test
    public void test_changeSessionId() throws IOException, ServletException {
        String ATTR_SID = "ATTR_SID";
        doInFilter((request, response) -> {
            HttpSession session = request.getSession(true);
            String preSessionId = request.getRequestedSessionId();
            request.setAttribute(ATTR_SID, preSessionId);
            request.changeSessionId();
        });

        String preSessionId = (String) mockRequest.getAttribute(ATTR_SID);
        assertThat(preSessionId).isNotBlank();

        nextRequest();

        doInFilter((request, response) -> {
            String sid = request.getRequestedSessionId();
            assertThat(preSessionId).isNotEqualTo(sid);
        });
    }

    @Test
    public void test_isRequestedSessionIdValid() throws IOException, ServletException {
        doInFilter((request, response) -> {
            assertThat(request.isRequestedSessionIdValid()).isFalse();

            request.getSession(true);
        });

        nextRequest();
        doInFilter((request, response) -> {
            assertThat(request.isRequestedSessionIdValid()).isTrue();
        });
    }

    private void setupRequest() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

    private void nextRequest() {
        Map<String, Cookie> cookies = new HashMap<>();
        if (mockRequest.getCookies() != null) {
            for (Cookie cookie : mockRequest.getCookies()) {
                cookies.put(cookie.getName(), cookie);
            }
        }

        if (mockResponse.getCookies() != null) {
            for (Cookie cookie : mockResponse.getCookies()) {
                cookies.put(cookie.getName(), cookie);
            }
        }

        setupRequest();

        this.mockRequest.setCookies(cookies.values().toArray(new Cookie[0]));
    }

    private void doInFilter(DoInFilter doInFilter) throws IOException, ServletException {
        MockFilterChain chain = new MockFilterChain(new HttpServlet() {
        }, new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                doInFilter.doFilter(httpServletRequest, httpServletResponse);
            }

            @Override
            public void destroy() {
            }
        });
        this.simpleSessionFilter.doFilter(mockRequest, mockResponse, chain);
    }

    interface DoInFilter {
        void doFilter(HttpServletRequest wrappedRequest,
                      HttpServletResponse wrappedResponse);

    }

}
