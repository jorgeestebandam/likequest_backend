package com.tiktokgame.likequest_backend.models.databse;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScoreDB {

    private final Connection conn;

    public GameScoreDB(Connection conn) {
        this.conn = conn;
    }

    public List<Map<String, Object>>  getScoresByUsername(String username) throws SQLException {
        List<Map<String, Object>> scores = new ArrayList<>();
        String sql = """
            SELECT *
            FROM game_scores
            WHERE username = ?
            ORDER BY created_at DESC
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Map<String, Object> score = new HashMap<>();
            score.put("id", rs.getInt("id"));
            score.put("pin", rs.getString("pin"));
            score.put("username", rs.getString("username"));
            score.put("points", rs.getInt("points"));
            score.put("correctAnswers", rs.getInt("correct_answers"));
            score.put("totalRounds", rs.getInt("total_rounds"));
            score.put("avgResponseTimeMs", rs.getDouble("avg_response_time_ms"));
            score.put("createdAt", rs.getTimestamp("created_at").toString());
            scores.add(score);
        }

        return scores;
    }
}