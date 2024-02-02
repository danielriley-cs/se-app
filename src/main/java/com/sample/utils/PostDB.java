package com.sample.utils;
import java.sql.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class PostDB {
    public static final String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";
    public static final String H2_DRIVER = "org.h2.Driver";

    private Connection con;
    private LoginInfo login;
    private boolean isLoggedIn;
    private String known_hosts_path;
    private String dbDriver;

    public PostDB(String known_hosts_path) {
        isLoggedIn = false;
        this.known_hosts_path = known_hosts_path;
    }

    public void Login(LoginInfo login, String driver) {
        this.login = login;
        this.dbDriver = driver;
        connect();
    }

    public void Logout() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Couldn't close SQL connection");
            }
        }
        isLoggedIn = false;
    }

    public boolean GetLoggedIn() {
        return isLoggedIn;
    }

    public String makeUrl(String host, int port, String dbName)
     throws ClassNotFoundException {
        if (dbDriver == null)
            return null;
        Class.forName(dbDriver);
        if (dbDriver.equals(MARIADB_DRIVER))
            return "jdbc:mariadb://" + host + ":" + port + "/" + dbName;
        else if (dbDriver.equals(H2_DRIVER))
            return "jdbc:h2:mem:" + dbName;
        else
            return null;
    }

    private void connect() {
        try {
            int dbPort = 3306;

            JSch jsch = new JSch();
            Session session;
            int realDBPort;
            String realDBHost;

            if (login.jumpHost != null) {
                session = jsch.getSession(login.jumpUser, login.jumpHost, 22);

                // Disabled until I can figure out how to prompt user to
                //   verify host's RSA key.
                //jsch.setKnownHosts(known_hosts_path);
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);

                session.setPassword(login.jumpPass);
                if (new File("~/.ssh/id_rsa").exists())
                    // Public key
                    jsch.addIdentity("~/.ssh/id_rsa");

                // Connect to SSH jump server
                session.connect();

                // Forward randomly chosen local port through the SSH channel to
                //   database host/port
                realDBPort = session.setPortForwardingL(0, login.dbHost,
                        dbPort);
                realDBHost = "localhost";
            } else {
                realDBPort = dbPort;
                realDBHost = login.dbHost;
            }
            try {
                con = DriverManager.getConnection(
                        makeUrl(realDBHost, realDBPort, login.dbDBName),
                        login.dbUser, login.dbPass);
            } catch (SQLException e) {
                System.out.println("DB doesn't exist, trying to create");
                try {
                    con = DriverManager.getConnection(
                            makeUrl(realDBHost, realDBPort, ""),
                            login.dbUser, login.dbPass);
                    con.createStatement().execute("CREATE DATABASE " +
                        login.dbDBName);
                    con = DriverManager.getConnection(
                            makeUrl(realDBHost, realDBPort, login.dbDBName),
                            login.dbUser, login.dbPass);
                } catch (SQLException ei) {
                    ei.printStackTrace();
                    System.out.println("Couldn't find or create database with given name");
                    throw ei;
                }
            }

            // If we got here, then the dbDBName exists and con is using it.

            isLoggedIn = true;
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't connect");
        }
    }

    boolean createTable() {
        if (!isLoggedIn)
            return false;
        try {
            Statement st = con.createStatement();
            String sqlQuery = "CREATE TABLE IF NOT EXISTS Posts(timeStamp DATE, text VARCHAR(200));";
            st.execute(sqlQuery);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Couldn't create SQL Table");
            return false;
        }
    }

    boolean addPost(String post) {
        if (!isLoggedIn)
            return false;
        if (post.length() == 0)
            return false;
        try {
            Statement st = con.createStatement();
            //MariaDB Date format "YYYY-MM-DD"
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
            LocalDateTime now = LocalDateTime.now();
            String timeStampToDB = dtf.format(now);
            String sqlQuery = "INSERT INTO Posts " +
                    "VALUES (DATE '"+ timeStampToDB + "', '" + post + "')";
            st.executeUpdate(sqlQuery);
            return true;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Couldn't add post to DB");
            return false;
        }
    }

    // Returns 'false' if post doesn't exist in DB
    boolean deletePost(String post) {
        if (!isLoggedIn)
            return false;
        try {
            Statement st = con.createStatement();
            String sqlQuery = "DELETE FROM Posts WHERE text = '" +
                post + "';";
            int rowsAffected = st.executeUpdate(sqlQuery);
            return rowsAffected != 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Couldn't delete post from DB");
            return false;
        }
    }

    String getAllPosts() {
        if (!isLoggedIn)
            return "Internal error: Not logged in";
        try {
            Statement st = con.createStatement();
            String sql = "SELECT * FROM Posts";
            ResultSet rs = st.executeQuery(sql);
            String result = "timeStamp" + "\t" + "text\n";
            // Extract result from ResultSet rs
            while(rs.next()){
                result += "" + rs.getString("timeStamp")
                        + "\t" + rs.getString("text") + "\n";
            }
            rs.close();
            return result;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Couldn't get all posts from DB");
            return "Internal error: 500";
        }
    }
}

