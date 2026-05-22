package com.tiktokgame.likequest_backend.controler.profile;
import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.models.databse.GameScoreDB;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/profile")
public class GameScoreController {
    @GetMapping("/{username}")
    public ResponseEntity<?> getScores(@PathVariable String username) {
        System.out.println("Getting scores for " + username);
        try {
            DBControler db = new DBControler();
            Connection conn =db.connectDB();
            GameScoreDB gameScoreDB = new GameScoreDB(conn);
            List<Map<String, Object>> scores = gameScoreDB.getScoresByUsername(username);
            conn.close();
            return ResponseEntity.ok(scores);
        } catch (SQLException e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }
}
