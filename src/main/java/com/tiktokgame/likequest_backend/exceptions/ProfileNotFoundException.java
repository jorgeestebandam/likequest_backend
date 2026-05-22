package com.tiktokgame.likequest_backend.exceptions;

public class ProfileNotFoundException extends Exception {
    private String username;
    public ProfileNotFoundException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
