package com.tiktokgame.likequest_backend.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private List<String> urlVideosLiked;

    public User(String username, List<String> urlVideosLiked) {
        this.username = username;
        this.urlVideosLiked = urlVideosLiked;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getUrlVideos() {
        return urlVideosLiked;
    }

    public void setUrlVideos(List<String> urlVideos) {
        this.urlVideosLiked = urlVideos;
    }
}
