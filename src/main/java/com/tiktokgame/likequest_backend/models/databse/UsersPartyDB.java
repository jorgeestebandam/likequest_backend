package com.tiktokgame.likequest_backend.models.databse;

import com.tiktokgame.likequest_backend.exceptions.PartyNotFound;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersPartyDB {
    private Connection conexion;

    public UsersPartyDB(Connection conexion) {
        this.conexion = conexion;
    }

    public void agregarUsuario(String username,String pin) throws SQLException {
        String sql = "INSERT INTO usersParty VALUES (?,?)";
        PreparedStatement pst = conexion.prepareStatement(sql);
        pst.setString(1, username);
        pst.setString(2, pin);
        pst.execute();
        pst.close();
    }

    public List<String> verListaUsuarios(String pin) throws SQLException {

        List<String> listaUsuariosPartida = new ArrayList<>();
        String sql = "SELECT username FROM usersParty WHERE pin = ?";
        PreparedStatement pst = conexion.prepareStatement(sql);
        pst.setString(1, pin);
        ResultSet rs = pst.executeQuery();
        while(rs.next()) {
            String username = rs.getString("username");
            listaUsuariosPartida.add(username);
        }
        pst.close();
        rs.close();
        return listaUsuariosPartida;
    }

    public boolean duplicidadUsuario(String username,String pin) throws SQLException {
        boolean duplicidad = false;
        List<String> listaUsuariosPartida = verListaUsuarios(pin);
        for(String usuario : listaUsuariosPartida) {
            if(username.equals(usuario)) {
                duplicidad = true;
            }
        }
        return duplicidad;
    }

    public void elminarUsuario(String username, String pin) throws SQLException, PartyNotFound {
        String sql = "DELETE FROM usersParty WHERE username = ? AND pin = ?";
        PreparedStatement pst = conexion.prepareStatement(sql);
        pst.setString(1, username);
        pst.setString(2, pin);
        int filasAfectadas = pst.executeUpdate();
        pst.close();
        if (filasAfectadas == 0) {
            throw new PartyNotFound(pin);
        }
    }
}
