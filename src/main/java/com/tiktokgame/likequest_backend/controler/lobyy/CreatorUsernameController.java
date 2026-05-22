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
public class CreatorUsernameController {
    @GetMapping("/{pin}/creator")

    public ResponseEntity<?> getCreator(@PathVariable String pin) {
        try {
            DBControler db = new DBControler();
            Connection conn = db.connectDB();
            PartidaDB partidaDB = new PartidaDB(conn);
            String creator = partidaDB.getCreatorUsername(pin);
            conn.close();

            if (creator == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Partida no encontrada"));
            }

            return ResponseEntity.ok(Map.of("creator", creator));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


}
