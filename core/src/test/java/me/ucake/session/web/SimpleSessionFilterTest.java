package me.ucake.session.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

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
        filter = new SimpleSessionFilter();
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
        mockFilterChain = new MockFilterChain(servlet, filter);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertThat(mockRequest.getAttribute(SimpleSessionFilter.class.getName().concat(".VISITED")))
                .isEqualTo(Boolean.TRUE);
    }

}
