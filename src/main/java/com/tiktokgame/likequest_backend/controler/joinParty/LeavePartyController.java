package com.tiktokgame.likequest_backend.controler.joinParty;

import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.exceptions.PartyNotFound;
import com.tiktokgame.likequest_backend.models.User;
import com.tiktokgame.likequest_backend.models.databse.UsersPartyDB;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/party")
public class LeavePartyController {

    @DeleteMapping("/leave")
    public ResponseEntity<?> leftParty(@RequestBody Map<String, Object> body) {
        String pin = (String) body.get("pin");
        Map<String, Object> userMap = (Map<String, Object>) body.get("user");
        String username = (String) userMap.get("username");
        Connection conexion = null;
        try{
            conexion = conectarDB();
            elimnarJugador(conexion,pin,username);
            return ResponseEntity.ok().body("Jugador salió de la partida");
        }
        catch(SQLException | PartyNotFound e){
            System.out.println("ERROR LEAVE PARTY : "+e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {

        try {
            if(conexion != null) conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    }

    public Connection conectarDB() throws SQLException {
        DBControler db = new DBControler();
        Connection conexion = db.connectDB();
        return conexion;
    }

    public void elimnarJugador(Connection conexion , String pin , String username) throws SQLException, PartyNotFound {
        UsersPartyDB  usersPartyDB = new UsersPartyDB(conexion);
        usersPartyDB.elminarUsuario(username,pin);
    }
}