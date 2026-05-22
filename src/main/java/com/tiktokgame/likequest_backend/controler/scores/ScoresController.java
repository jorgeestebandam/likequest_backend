package com.tiktokgame.likequest_backend.controler.scores;
import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.models.databse.GameScoresDB;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/scores")
public class ScoresController {

    /// POST /api/scores/{pin} — guarda o actualiza la puntuación de un jugador
    @PostMapping("/{pin}")
    public ResponseEntity<?> saveScore(
            @PathVariable String pin,
            @RequestBody Map<String, Object> body) throws SQLException {

        String username = (String) body.get("username");
        int points = (int) body.get("points");
        int correctAnswers = (int) body.get("correctAnswers");
        int totalRounds = (int) body.get("totalRounds");
        double avgResponseTimeMs = ((Number) body.get("avgResponseTimeMs")).doubleValue();

        DBControler db = new DBControler();
        Connection conn = db.connectDB();
        GameScoresDB scoresDB = new GameScoresDB(conn);
        scoresDB.upsertScore(pin, username, points, correctAnswers, totalRounds, avgResponseTimeMs);
        conn.close();

        return ResponseEntity.ok(Map.of("status", "saved"));
    }

    /// GET /api/scores/{pin} — devuelve la clasificación completa ordenada
    @GetMapping("/{pin}")
    public ResponseEntity<?> getScores(@PathVariable String pin) throws SQLException {
        DBControler db = new DBControler();
        Connection conn = db.connectDB();
        GameScoresDB scoresDB = new GameScoresDB(conn);
        List<Map<String, Object>> scores = scoresDB.getScoresByPin(pin);
        conn.close();

        return ResponseEntity.ok(scores);
    }

    /// DELETE /api/scores/{pin} — borra puntuaciones al reiniciar partida
    @DeleteMapping("/{pin}")
    public ResponseEntity<?> deleteScores(@PathVariable String pin) throws SQLException {
        DBControler db = new DBControler();
        Connection conn = db.connectDB();
        GameScoresDB scoresDB = new GameScoresDB(conn);
        scoresDB.deleteScoresByPin(pin);
        conn.close();

        return ResponseEntity.ok(Map.of("status", "deleted"));
    }
}