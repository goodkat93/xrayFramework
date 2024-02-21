package com.ugasoft.models;

public class DefaultUserData {

    protected String userName;
    protected String password;

    public DefaultUserData() {
        this.userName = "admin";
        this.password = "admin";
    }

    public DefaultUserData(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public DefaultUserData(String userName) {
        this.userName = userName;
        this.password = "admin";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
