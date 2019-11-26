package com.tagalong.tagalong.models;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<Conversation> conversationList;
    private List<String> usernames;
    private String roomID;
    private String userID;

    public Chat(String roomID) {
        conversationList = new ArrayList<>();
        usernames = new ArrayList<>();
        this.roomID = roomID;
    }

    public Chat(List<Conversation> conversationList, List<String> usernames, String roomID) {
        this.conversationList = conversationList;
        this.usernames = usernames;
        this.roomID = roomID;
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void addConversation(Conversation conversation){
        conversationList.add(conversation);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
