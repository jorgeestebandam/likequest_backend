package com.tiktokgame.likequest_backend.models.joinParty;

import com.tiktokgame.likequest_backend.models.User;

public class JoinPartyRequest {
    private String pin;
    private User user;

    public JoinPartyRequest(String pin, User user) {
        this.pin = pin;
        this.user = user;
    }


    public JoinPartyRequest() {
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
