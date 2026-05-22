package com.tiktokgame.likequest_backend.models.databse;

import java.sql.*;
import java.util.*;

public class GameScoresDB {

    private final Connection conexion;

    public GameScoresDB(Connection conexion) {
        this.conexion = conexion;
    }

    /// Guarda o actualiza la puntuación de un jugador en una partida
    public void upsertScore(String pin, String username, int points,
                            int correctAnswers, int totalRounds,
                            double avgResponseTimeMs) throws SQLException {
        String sql = """
            INSERT INTO game_scores (pin, username, points, correct_answers, total_rounds, avg_response_time_ms)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                points = VALUES(points),
                correct_answers = VALUES(correct_answers),
                total_rounds = VALUES(total_rounds),
                avg_response_time_ms = VALUES(avg_response_time_ms)
        """;
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        stmt.setString(2, username);
        stmt.setInt(3, points);
        stmt.setInt(4, correctAnswers);
        stmt.setInt(5, totalRounds);
        stmt.setDouble(6, avgResponseTimeMs);
        stmt.executeUpdate();
        stmt.close();
    }

    /// Devuelve la clasificación completa de una partida ordenada por puntos
    public List<Map<String, Object>> getScoresByPin(String pin) throws SQLException {
        String sql = """
            SELECT username, points, correct_answers, total_rounds, avg_response_time_ms
            FROM game_scores
            WHERE pin = ?
            ORDER BY points DESC
        """;
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        ResultSet rs = stmt.executeQuery();

        List<Map<String, Object>> scores = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("username", rs.getString("username"));
            row.put("points", rs.getInt("points"));
            row.put("correctAnswers", rs.getInt("correct_answers"));
            row.put("totalRounds", rs.getInt("total_rounds"));
            row.put("avgResponseTimeMs", rs.getDouble("avg_response_time_ms"));
            scores.add(row);
        }

        rs.close();
        stmt.close();
        return scores;
    }

    /// Borra las puntuaciones de una partida (útil al reiniciar)
    public void deleteScoresByPin(String pin) throws SQLException {
        String sql = "DELETE FROM game_scores WHERE pin = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, pin);
        stmt.executeUpdate();
        stmt.close();
    }
}
