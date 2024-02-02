package com.sample.utils;

import java.util.LinkedList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Example of the alternative to Mockito
// WARNING: DOESN'T WORK.
public class MockLoggerPost extends LoggerPost {
    private class MockPost {
        public String timeStamp;
        public String post;
        public MockPost(String ts, String p) { timeStamp = ts; post = p; }
    }
    private boolean isLoggedIn = false;
    private LinkedList<MockPost> posts = new LinkedList<MockPost>();

    public MockLoggerPost(String ign1, String ign2) {
        super(ign1, ign2);
        return;
    }

    public void Login(LoginInfo login) {
        isLoggedIn = true;
    }

    public void Logout() {
        isLoggedIn = false;
    }

    public boolean GetLoggedIn() {
        return isLoggedIn;
    }

    public void PostToDB(String post) {
        if (!isLoggedIn)
            return;
        posts.add(new MockPost(
            DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDateTime.now()),
            post));
    }

    public String GetPostsFromDB() {
        if (!isLoggedIn)
            return "Internal error: Not logged in";
        String result = "timeStamp\ttext\n";
        for (MockPost p : posts) {
            result += p.timeStamp + "\t" + p.post + "\n";
        }
        return result;
    }

    public boolean DeletePostFromDB(String post) {
        if (!isLoggedIn)
            return false;

        for (int i = 0; i < posts.size(); ++i) {
            if (posts.get(i).post.equals(post)) {
                posts.remove(i);
                return true;
            }
        }
        return false;
    }
}
