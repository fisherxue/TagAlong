package com.tagalong.tagalong.models;

import java.io.Serializable;

/**
 * Data Structure Login - a login profile class (essential elements of profile)
 */
public class Login implements Serializable {
    private String username;
    private String password;
    private String fbToken;
    private String firstName;
    private String lastName;
    private String email;
    private String id;

    //getters
    public String getEmailId() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmailId(String emailId) {
        this.email = emailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFbToken() {
        return fbToken;
    }


    //Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }
}
