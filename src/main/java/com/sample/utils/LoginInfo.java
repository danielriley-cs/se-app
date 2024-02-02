package com.sample.utils;

public class LoginInfo {
    public String dbHost, dbDBName, dbUser, dbPass;

    // Pass 'null' to jumpHost (others ignored) if no jump is needed
    public String jumpHost, jumpUser, jumpPass;
    
    public LoginInfo(String dbHost, String dbDBName, String dbUser,
        String dbPass, String jumpHost, String jumpUser, String jumpPass) {
        this.dbHost = dbHost;
        this.dbDBName = dbDBName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.jumpHost = jumpHost;
        this.jumpUser = jumpUser;
        this.jumpPass = jumpPass;
    }
}
