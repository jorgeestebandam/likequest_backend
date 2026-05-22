package com.tiktokgame.likequest_backend.models.databse;

import com.tiktokgame.likequest_backend.models.joinParty.JoinPartyRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfPartidaDB {
    private Connection conexion;

    public ConfPartidaDB(Connection conexion) {
        this.conexion = conexion;
    }

    public void crearConfigPorDefecto(String pin) throws SQLException {
        String sql = "INSERT INTO party_conf (pin) VALUES (?)";

        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        stmt.executeUpdate();
        stmt.close();
    }

    public void updateConfig(String pin, int rounds, int time, int maxUsers, boolean joinLater) throws SQLException {
        String sql = """
                     UPDATE party_conf SET number_rounds = ?,   choice_time = ?,  max_users = ?,
                        join_later = ?
                         WHERE pin = ?
                """;

        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setInt(1, rounds);
        stmt.setInt(2, time);
        stmt.setInt(3, maxUsers);
        stmt.setBoolean(4, joinLater);
        stmt.setString(5, pin);

        stmt.executeUpdate();
        stmt.close();
    }

    public boolean validarJoinLater(String pin ) throws SQLException {
        boolean joinLater = false;
        String sql = "SELECT join_later FROM party_conf WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
             joinLater = rs.getBoolean(1);
        }
        rs.close();
        stmt.close();
        return joinLater;
    }

    public int obtenerMaxUsers(String pin) throws SQLException {
        int maxUsers= 0 ;
        String sql = "SELECT max_users FROM party_conf WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            maxUsers = rs.getInt(1);
        }

        return maxUsers;
    }

    public int getTotalRounds(String pin) throws SQLException {
        int rounds=0;
        String sql = "SELECT number_rounds FROM party_conf WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            rounds = rs.getInt(1);
        }
        rs.close();
        stmt.close();
        return rounds;
    }

    public int getChoiceTime(String pin) throws SQLException {
        int choiceTime=0;
        String sql = "SELECT choice_time FROM party_conf WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            choiceTime = rs.getInt(1);
        }
        rs.close();
        stmt.close();
        return choiceTime;
    }

}
