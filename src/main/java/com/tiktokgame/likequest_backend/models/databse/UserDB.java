package com.tiktokgame.likequest_backend.models.databse;

import com.tiktokgame.likequest_backend.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDB {
    private Connection conexion;
    private User user;

    public UserDB(Connection conexion,User user) {
        this.conexion = conexion;
        this.user = user;
    }

    public boolean insertUser() throws SQLException {
        boolean insertado = false;
        boolean existe = existUser();
        if (!existe) {
            String sql = "INSERT INTO users (username) VALUES (?)";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
             stmt.executeUpdate();
             insertado = true;
             stmt.close();
        }
        return insertado;
    }

    public boolean existUser() throws SQLException {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, user.getUsername());
        ResultSet res = stmt.executeQuery();
        if (res.next()) {
            int count = res.getInt(1);
            if (count > 0) {
                existe = true;
            }
        }
        res.close();
        stmt.close();
        return existe;
    }


}
