package me.ucake.session.web;

import me.ucake.session.jvm.MapSessionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by alexqdjay on 2017/8/20.
 */
public class SimpleSessionFilterTest {

    private SimpleSessionFilter filter;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private MockFilterChain mockFilterChain;


    @Before
    public void setup() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        filter = new SimpleSessionFilter(new MapSessionRepository());
    }


    // 测试是否经过filter以后request被替换成SimpleSessionRequest
    @Test
    public void test_filter_wrapper() throws IOException, ServletException {
        HttpServlet servlet = new HttpServlet() {
            @Override
            public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
                assertThat(req).isInstanceOf(SimpleSessionRequest.class);
                super.service(req, res);
            }
        };
        AtomicReference<ServletRequest> targetRequest = new AtomicReference<>();
        mockFilterChain = new MockFilterChain(servlet, filter, new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                targetRequest.set(request);
                chain.doFilter(request, response);
                Boolean visited = (Boolean) mockRequest.getAttribute("me.ucake.session.web.SimpleSessionFilter.VISITED");
                assertThat(visited).isEqualTo(Boolean.TRUE);
            }
            @Override
            public void destroy() {
            }
        });

        Boolean visited = (Boolean) mockRequest.getAttribute("me.ucake.session.web.SimpleSessionFilter.VISITED");

        assertThat(visited).isNull();

        mockFilterChain.doFilter(mockRequest, mockResponse);

        assertThat(mockRequest.getAttribute(SimpleSessionFilter.class.getName().concat(".VISITED")))
                .isNull();

        assertThat(targetRequest.get()).isInstanceOf(SimpleSessionRequest.class);
    }

    @Test(expected = ServletException.class)
    public void test_requestIsNull() throws IOException, ServletException {
        filter.doFilter(null, null, null);
    }

    @Test
    public void test_init() throws ServletException {
        filter.init(null);

        filter.destroy();
    }

}
