package com.tiktokgame.likequest_backend.models.databse;

import com.tiktokgame.likequest_backend.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class VideosDB {
    private Connection conexion;
    private User user ;

    public VideosDB(Connection conexion, User user) {
        this.conexion = conexion;
        this.user = user;

    }

    public VideosDB(Connection conexion) {
        this.conexion = conexion;
    }

    public void insertVideos() throws SQLException {
        List<String> urlVideos = user.getUrlVideos();

        String sql = "INSERT INTO videos (url, username) VALUES (?, ?)";
        PreparedStatement stmt = conexion.prepareStatement(sql);

        for (String url : urlVideos) {
            stmt.setString(1, url);
            stmt.setString(2, user.getUsername());
            stmt.addBatch(); // guarda en cola todo los videos , para subirlo todos a la vez
        }

        stmt.executeBatch();
        stmt.close();
    }

    public void updateVideos() throws SQLException {

        // Borrar vídeos antiguos del usuario
        String deleteSql = "DELETE FROM videos WHERE username = ?";
        PreparedStatement deleteStmt = conexion.prepareStatement(deleteSql);
        deleteStmt.setString(1, user.getUsername());
        deleteStmt.executeUpdate();
        deleteStmt.close();

        insertVideos();
    }

    public String getRandomVideo(String username, Set<String> usedUrls) throws SQLException {
        if (usedUrls.isEmpty()) {
            String sql = "SELECT url FROM videos WHERE username = ? AND url LIKE '%/video/%' ORDER BY RAND() LIMIT 1";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            String url = rs.next() ? rs.getString(1) : null;
            rs.close(); stmt.close();
            return url;
        }

        String placeholders = String.join(",", Collections.nCopies(usedUrls.size(), "?"));
        String sql = "SELECT url FROM videos WHERE username = ? AND url LIKE '%/video/%' AND url NOT IN (" + placeholders + ") ORDER BY RAND() LIMIT 1";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, username);
        int i = 2;
        for (String url : usedUrls) {
            stmt.setString(i++, url);
        }
        ResultSet rs = stmt.executeQuery();
        String url = rs.next() ? rs.getString(1) : null;
        rs.close(); stmt.close();
        return url;
    }

}
