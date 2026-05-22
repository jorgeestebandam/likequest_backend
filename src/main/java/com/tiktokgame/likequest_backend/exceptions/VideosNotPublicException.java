package com.tiktokgame.likequest_backend.exceptions;

public class VideosNotPublicException extends Exception {
    String username;
    public VideosNotPublicException(String username) {
        this.username = username;

    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
