package com.tiktokgame.likequest_backend.models.databse;

import com.tiktokgame.likequest_backend.exceptions.PartyNotFound;
import com.tiktokgame.likequest_backend.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class PartidaDB {

    private Connection conexion;

    public PartidaDB() {
    }

    public PartidaDB(Connection conexion) {
        this.conexion = conexion;
    }

    public String generarPin() throws SQLException {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final int PIN_LENGTH = 10;
        Random random = new Random();
        String pin;
        boolean existe;
        do {
            StringBuilder sb = new StringBuilder(PIN_LENGTH);
            for (int i = 0; i < PIN_LENGTH; i++) {
                int index = random.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(index));
            }
            pin = sb.toString();
            existe = existPin(pin);

        } while (existe);
        return pin;
    }
    public boolean existPin(String pin) throws SQLException {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM party WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet res = stmt.executeQuery();
        if (res.next()) {
            int count = res.getInt(1);
            if (count == 1) {
                existe = true;
            }
        }
        res.close();
        stmt.close();
        return existe;
    }

    public void crearPartida(String pin , User user) throws SQLException {
        String sql = "INSERT INTO party (pin, creator_username) VALUES (?, ?)";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        stmt.setString(2, user.getUsername());
        stmt.executeUpdate();
        stmt.close();
    }

    public String validarEstado(String pin) throws SQLException {
        String estadoDevolucion = null;
        String sql = "SELECT estado FROM party WHERE pin = ? ";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet res = stmt.executeQuery();
       if (res.next()) {
           String estado  = res.getString(1);
            if (estado.equals("esperando") || estado.equals("jugando")) {
                estadoDevolucion = estado;
            }
        }
       res.close();
       stmt.close();
       return estadoDevolucion;
    }

    public boolean borrarPartida(String pin) throws SQLException{
        boolean existe = false;
        existe = existPin(pin);
        if (existe) {
            String sql = "DELETE FROM party WHERE pin = ?";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, pin);
            stmt.executeUpdate();
            stmt.close();
        }
        return existe;
    }

    public String getCreatorUsername(String pin) throws SQLException {
        String creatorUsername = null;
        String sql = "SELECT creator_username FROM party WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            creatorUsername = rs.getString(1);
        }
        rs.close();
        stmt.close();
        return creatorUsername;
    }
    public String getStateParty(String pin) throws SQLException {
        String state = null;
        String sql = "SELECT estado FROM party WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            state = rs.getString(1);
        }
        rs.close();
        stmt.close();
        return state;
    }
    public void setStateParty(String pin, String state) throws SQLException {
        String sql = "UPDATE party SET estado = 'jugando' WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        stmt.executeUpdate();
        stmt.close();
    }
}
