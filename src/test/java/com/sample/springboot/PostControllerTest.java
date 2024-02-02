package com.sample.springboot;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import com.sample.utils.LoginInfo;
import com.sample.utils.PostDB;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.ui.Model;

import com.sample.utils.MockLoggerPost;
import com.sample.utils.LoggerPost;


public class PostControllerTest {
    @Spy private PostController pc;
    @Mock private LoggerPost lp;

    @Mock private HttpSession sesh;
    @Mock private HttpServletRequest req;
    @Mock private Model model;

    @BeforeEach
    public void setupEach() {
        pc = spy(new PostController());
        lp = mock(LoggerPost.class);
        sesh = mock(HttpSession.class);
        req = mock(HttpServletRequest.class);
        model = mock(Model.class);
        doReturn(lp).when(pc).getLogger(any());
    }

    @AfterEach
    public void teardownEach() {
        pc = null;
        lp = null;
        sesh = null;
        req = null;
        model = null;
    }

    @Test
    public void testLogin() {
        when(lp.GetLoggedIn()).thenReturn(true);
        //when(req.getParameter("dbHost")).thenReturn("localhost");
        //when(req.getParameter("dbDBName")).thenReturn("testdb");
        assertEquals("index", pc.Login(model, sesh, req));
        verify(lp).Login(any(), PostDB.H2_DRIVER);

        //LoginInfo li = new LoginInfo("localhost", "testdb", ...);
        //verify(lp).Login(li);
    }

    @Test
    public void testAddLoggedIn() {
        when(lp.GetLoggedIn()).thenReturn(true);
        assertEquals("index", pc.AddPost("post1", model, sesh));
        verify(lp).PostToDB("post1");
    }

    @Test
    public void testAddLoggedOut() {
        when(lp.GetLoggedIn()).thenReturn(false); // The default
        assertEquals("index", pc.AddPost("post1", model, sesh));
        verify(lp, never()).PostToDB(anyString());
    }
}
