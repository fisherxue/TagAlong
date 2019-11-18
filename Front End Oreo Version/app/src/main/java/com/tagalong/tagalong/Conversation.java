package com.tagalong.tagalong;

public class Conversation {
    private String userID;
    private String message;

    public Conversation(String username, String message) {
        this.userID = username;
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
