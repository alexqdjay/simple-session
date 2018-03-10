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
import java.util.Arrays;
import java.util.Collections;
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
    public void test_sessionId() throws IOException, ServletException {
        String[] ids = new String[1];
        doInFilter((request, response) -> {
            String sessionId = request.getSession().getId();
            assertThat(sessionId).isNotNull();
            assertThat(sessionId).isEqualTo(request.getSession().getId());
            ids[0] = sessionId;
        });

        setSessionCookie(ids[0]);
        doInFilter((request, response) -> {
            assertThat(ids[0]).isEqualTo(request.getSession().getId());
        });
    }

    @Test
    public void test_servletContext() throws IOException, ServletException {
        doInFilter((request, response) -> {
            assertThat(request.getServletContext()).isSameAs(request.getSession().getServletContext());
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

    @Test
    public void test_maxInactiveIntervalDefault() throws IOException, ServletException {
        doInFilter((request, response) -> {
            int inactive = request.getSession().getMaxInactiveInterval();
            assertThat(inactive).isEqualTo(1800);
        });
    }

    @Test
    public void test_setInactiveInterval() throws IOException, ServletException {
        final int interval = 10001;
        doInFilter((request, response) -> {
            request.getSession().setMaxInactiveInterval(interval);
            assertThat(request.getSession().getMaxInactiveInterval()).isEqualTo(interval);
        });

        nextRequest();

        doInFilter((request, response) -> {
            assertThat(request.getSession().getMaxInactiveInterval()).isEqualTo(interval);
        });
    }

    @Test
    public void test_setAttributes() throws IOException, ServletException {
        String ATTR = "ATTR";
        String value = "12233";
        doInFilter((request, response) -> {
            request.getSession().setAttribute(ATTR, value);
            assertThat(Collections.list(request.getSession().getAttributeNames()))
                    .containsOnly(ATTR);

        });

        nextRequest();
        doInFilter((request, response) -> {
            assertThat(request.getSession().getAttribute(ATTR)).isEqualTo(value);
            assertThat(Collections.list(request.getSession().getAttributeNames()))
                    .containsOnly(ATTR);

            request.getSession().removeAttribute(ATTR);
            assertThat(request.getSession().getAttribute(ATTR)).isNull();
        });

        nextRequest();
        doInFilter((request, response) -> {
            assertThat(request.getSession().getAttribute(ATTR)).isNull();
        });
    }

    @Test
    public void test_setValue() throws IOException, ServletException {
        String ATTR = "ATTR";
        String value = "12233";
        doInFilter((request, response) -> {
            request.getSession().putValue(ATTR, value);
            assertThat(request.getSession().getValue(ATTR)).isEqualTo(value);
            assertThat(Arrays.asList(request.getSession().getValueNames()))
                    .containsOnly(ATTR);
        });

        nextRequest();
        doInFilter((request, response) -> {
            assertThat(request.getSession().getValue(ATTR)).isEqualTo(value);
            assertThat(Arrays.asList(request.getSession().getValueNames()))
                    .containsOnly(ATTR);

            request.getSession().removeValue(ATTR);
            assertThat(request.getSession().getValue(ATTR)).isNull();
        });

        nextRequest();
        doInFilter((request, response) -> {
            assertThat(request.getSession().getValue(ATTR)).isNull();
        });
    }

    @Test
    public void test_createIsNewTrue() throws IOException, ServletException {
        doInFilter((request, response) -> {
            assertThat(request.getSession().isNew()).isTrue();
            assertThat(request.getSession().isNew()).isTrue();
        });

        nextRequest();

        doInFilter((request, response) -> {
            assertThat(request.getSession().isNew()).isFalse();
        });
    }

    @Test
    public void test_setCookieIfChanged() throws IOException, ServletException {
        doInFilter((request, response) -> {
            request.getSession();
        });
        assertThat(mockResponse.getCookie(CookieBasedTransaction.COOKIE_NAME_SESSION)).isNotNull();

        nextRequest();

        this.mockResponse.reset();
        doInFilter((request, response) -> {
            request.changeSessionId();
            assertThat(request.getSession().isNew()).isFalse();
        });
        assertThat(mockResponse.getCookie(CookieBasedTransaction.COOKIE_NAME_SESSION)).isNotNull();
    }

    @Test
    public void test_getSessionNew() throws IOException, ServletException {
        doInFilter((request, response) -> {
            request.getSession();
        });

        assertSessionNew();
    }

    @Test
    public void test_getSessionFalseNew() throws IOException, ServletException {
        doInFilter((request, response) -> {
            request.getSession(false);
        });

        Cookie cookie = mockResponse.getCookie(CookieBasedTransaction.COOKIE_NAME_SESSION);
        assertThat(cookie).isNull();
    }

    @Test
    public void test_isRequestedValid() throws IOException, ServletException {
        doInFilter((request, response) -> {
            request.getSession();
        });

        nextRequest();
        this.mockRequest.setRequestedSessionIdValid(false);
        doInFilter((request, response) -> {
            assertThat(request.isRequestedSessionIdValid()).isTrue();
        });
    }

    @Test
    public void test_changeSessionIdNoSession() throws IOException, ServletException {
        doInFilter((request, response) -> {
            try {
                request.changeSessionId();
                fail("expected exception");
            } catch (IllegalStateException e) {}
        });
    }

    @Test
    public void test_isRequestedValidSessionFalseInvalidId() throws IOException, ServletException {
        setSessionCookie("invalid");
        mockRequest.setRequestedSessionIdValid(false);
        doInFilter((request, response) -> {
            assertThat(request.isRequestedSessionIdValid()).isFalse();
        });
    }

    @Test
    public void test_isRequestedValidSessionFalse() throws IOException, ServletException {
        mockRequest.setRequestedSessionIdValid(false);
        doInFilter((request, response) -> {
            assertThat(request.isRequestedSessionIdValid()).isFalse();
        });
    }

    @Test
    public void test_securitySet() throws IOException, ServletException {
        mockRequest.setSecure(false);
        doInFilter((request, response) -> {
            request.getSession();
        });
        Cookie cookie = getSessionCookie();

        assertThat(cookie.getSecure()).isTrue();
    }

    @Test
    public void test_context() throws IOException, ServletException {
        doInFilter((request, response) -> {
            HttpSessionContext context = request.getSession().getSessionContext();
            assertThat(context).isNotNull();
            assertThat(context.getSession("xxx")).isNull();
            assertThat(context.getIds()).isNotNull();
            assertThat(context.getIds().hasMoreElements()).isFalse();
        });
    }

    @Test
    public void test_sessionInvalid() throws IOException, ServletException {
        doInFilter((request, response) -> {
            request.getSession().invalidate();
            try {
                request.getSession().invalidate();
                fail("excepted exception");
            } catch (IllegalStateException e) {}
        });
    }


    private void assertSessionNew() {
        Cookie cookie = getSessionCookie();
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotEqualTo("INVALID");
        assertThat(cookie.getMaxAge()).isEqualTo(-1);
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    private Cookie getSessionCookie() {
        return mockResponse.getCookie(CookieBasedTransaction.COOKIE_NAME_SESSION);
    }

    private void setSessionCookie(String sessionId) {
        this.mockRequest.setCookies(new Cookie("ssession", sessionId));
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
