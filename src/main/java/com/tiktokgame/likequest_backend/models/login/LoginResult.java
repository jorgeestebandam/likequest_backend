package com.tiktokgame.likequest_backend.models.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LoginResult {
    private String username;
    private String isPublic;
    private List<String> urlVideosLiked;

    public LoginResult(String username, String isPublic,List<String> urlVideosLiked) {
        this.username = username;
        this.isPublic = isPublic;
        this.urlVideosLiked = urlVideosLiked;
    }
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @JsonProperty("isPublic")
    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }
    @JsonProperty("urlVideosLiked")
    public List<String> getListVideosLiked() {
        return urlVideosLiked;
    }

    public void setIsVideoPublic(List<String> urlVideosLiked) {
        this.urlVideosLiked = urlVideosLiked;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "username='" + username + '\'' +
                ", isPublic='" + isPublic + '\'' +
                ", urlVideosLiked=" + urlVideosLiked +
                '}';
    }
}
