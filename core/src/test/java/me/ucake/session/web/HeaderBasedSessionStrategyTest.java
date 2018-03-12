package me.ucake.session.web;

import me.ucake.session.core.UUIDGen;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Created by alexqdjay on 2018/3/12.
 */
public class HeaderBasedSessionStrategyTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private HeaderBasedSessionStrategy strategy;
    private String headerName;
    private Session session;

    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        strategy = new HeaderBasedSessionStrategy();
        headerName = "x-auth-token";
        session = mock(Session.class);
    }

    @Test
    public void test_configHeaderName() {
        try {
            strategy.setTokenHeaderName(null);
            fail("can not be null");
        } catch (IllegalArgumentException e){}

        try {
            strategy.setTokenHeaderName("");
            fail("can not be empty");
        } catch (IllegalArgumentException e){}

        try {
            strategy.setTokenHeaderName(" ");
            fail("can not be empty");
        } catch (IllegalArgumentException e){}
    }

    @Test
    public void test_getSessionIdNull() {
        assertThat(strategy.getRequestedSessionId(request)).isNull();
    }

    @Test
    public void test_getSessionId() {
        setSessionId("123");

        assertThat(strategy.getRequestedSessionId(request)).isEqualTo("123");
    }

    @Test
    public void test_customHeaderName() {
        strategy.setTokenHeaderName("xxxx");

        setSessionId("123");

        assertThat(strategy.getRequestedSessionId(request)).isNull();

        request.addHeader("xxxx", "22");
        assertThat(strategy.getRequestedSessionId(request)).isEqualTo("22");
    }

    @Test
    public void test_onNew() {
        when(session.getId()).thenReturn("123");
        this.strategy.onNewSession(this.session, this.request, this.response);
        assertThat(getSessionId()).isEqualTo(this.session.getId());
    }

    @Test
    public void test_onDeleteSession() throws Exception {
        this.strategy.onInvalidateSession(this.request, this.response);
        assertThat(getSessionId()).isEmpty();
    }


    private void setSessionId(String id) {
        request.addHeader(headerName, id);
    }

    public String getSessionId() {
        return this.response.getHeader(this.headerName);
    }


}
