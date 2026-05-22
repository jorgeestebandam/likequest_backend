package com.tiktokgame.likequest_backend.controler.lobyy;


import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.models.databse.PartidaDB;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/party")
public class StatePartyController {

   // ENDPOINT GET STATE
    @GetMapping("/{pin}/state")
    public ResponseEntity<?> getState(@PathVariable String pin) {
        try {
            DBControler db = new DBControler();
            Connection conn = db.connectDB();
            PartidaDB partidaDB = new PartidaDB(conn);
            String estado = partidaDB.getStateParty(pin);
            conn.close();

            if (estado == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Partida no encontrada"));
            }

            return ResponseEntity.ok(Map.of("estado", estado));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    //ENDPOINT SET STATE
    @PutMapping("/{pin}/start")
    public ResponseEntity<?> iniciarPartida(@PathVariable String pin) {
        try {
            DBControler db = new DBControler();
            Connection conn = db.connectDB();
            PartidaDB partidaDB = new PartidaDB(conn);
            partidaDB.setStateParty(pin, "jugando");
            conn.close();
            return ResponseEntity.noContent().build(); // 204
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
