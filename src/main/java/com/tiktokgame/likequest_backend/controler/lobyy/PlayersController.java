package com.tiktokgame.likequest_backend.controler.lobyy;

import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.models.databse.UsersPartyDB;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/party")
public class PlayersController {
    @GetMapping("/{pin}/players")
    public ResponseEntity<?> getPlayers(@PathVariable String pin) throws SQLException {
        DBControler db = new DBControler();
        Connection conn = db.connectDB();
        UsersPartyDB usersPartyDB = new UsersPartyDB(conn);
        List<String> jugadores = usersPartyDB.verListaUsuarios(pin);
        conn.close();

        if (jugadores == null) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "PIN_NOT_FOUND",
                            "message", "No se ha encontrado partida con ese PIN"
                    )
            );
        }

        return ResponseEntity.ok(Map.of("jugadores", jugadores));
    }
}
