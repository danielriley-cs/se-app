package com.sample.utils;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;

public class PostDBTest {
    private PostDB db;

    @BeforeEach
    public void setupEach() throws SQLException,ClassNotFoundException {
        db = new PostDB("");

        // Random DBName so each test runs independently
        LoginInfo li = new LoginInfo(
                "", "" + Math.random(), "", "", null, null, null);
        // Use in-memory database
        db.Login(li, PostDB.H2_DRIVER);
        assertTrue(db.GetLoggedIn());
    }

    @AfterEach
    public void teardownEach() {
        db.Logout();
        assertFalse(db.GetLoggedIn());
        db = null;
    }

    @Test
    public void testAdd() {
        assertFalse(db.getAllPosts().contains("post1"));
        assertTrue(db.addPost("post1"));
        assertTrue(db.getAllPosts().contains("post1"));
    }

    @Test
    public void testAddNull() {
        assertEquals(db.getAllPosts(), "timeStamp\ttext\n");
        assertFalse(db.addPost(null));
        assertEquals(db.getAllPosts(), "timeStamp\ttext\n");
    }

    @Test
    public void testMultiple() {
        // Test if multiple tests work
        assertEquals(db.getAllPosts(), "timeStamp\ttext\n");
    }
}
