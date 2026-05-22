package com.tiktokgame.likequest_backend.controler.createParty;

import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.models.User;
import com.tiktokgame.likequest_backend.models.databse.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController

@RequestMapping("/api/party")
public class CreatePartyController {
    @PostMapping("/create")
    public ResponseEntity<?> createParty(@RequestBody User user) {
        Connection conexion;
        String pin;
        try {
            conexion = conectarDB();
            gestionUsers(conexion, user);
            pin = gestionPartida(conexion, user);
            gestionConfiguaracionPartida(pin,conexion);
            agregarUsuarioListadoDePartidas(conexion,user.getUsername(),pin);
            desconectarDB(conexion);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(
                    Map.of(
                            "error", "DB_ERROR",
                            "message", e.getMessage()
                    )
            );
        }
        return ResponseEntity.ok(Map.of(
                "pin", pin,
                "creator", user.getUsername()
        ));
    }

    public Connection conectarDB() throws SQLException {
        DBControler db = new DBControler();
        Connection conexion = db.connectDB();
        return conexion;
    }

    public void gestionUsers(Connection conexion, User user) throws SQLException {
        UserDB userDB = new UserDB(conexion, user);
        boolean insertado = userDB.insertUser();
        VideosDB videosDB = new VideosDB(conexion, user);
        if (insertado) {
            videosDB.insertVideos();
        } else {
            videosDB.updateVideos();
        }
    }

    public String gestionPartida(Connection conexion, User user) {
        PartidaDB partidaDB = new PartidaDB(conexion);
        String pin = "";
        try {
            pin = partidaDB.generarPin();
            partidaDB.crearPartida(pin, user);
        } catch (SQLException e) {
            System.out.println("Error al generar pin");
        }
        return pin;
    }

    public void gestionConfiguaracionPartida(String pin,Connection conexion)
            throws SQLException {
        ConfPartidaDB conf = new ConfPartidaDB(conexion);
        conf.crearConfigPorDefecto(pin);
    }

    public void agregarUsuarioListadoDePartidas(Connection conexion,String username , String pin)
            throws SQLException {
        UsersPartyDB usersPartyDB = new UsersPartyDB(conexion);
        usersPartyDB.agregarUsuario(username, pin);
    }

    public void desconectarDB(Connection conexion) throws SQLException {
        DBControler db = new DBControler();
        db.closeDBConnection(conexion);
    }
}
