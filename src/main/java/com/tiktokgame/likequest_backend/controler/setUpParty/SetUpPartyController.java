package com.tiktokgame.likequest_backend.controler.setUpParty;

import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.models.databse.ConfPartidaDB;
import com.tiktokgame.likequest_backend.models.databse.UsersPartyDB;
import com.tiktokgame.likequest_backend.models.databse.VideosDB;
import com.tiktokgame.likequest_backend.services.YtDlpExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/party")
public class SetUpPartyController {

    @Autowired
    private YtDlpExtractor tiktokExtractor;
    private static final Map<String, Map<String, Object>> partidasListas = new ConcurrentHashMap<>();

    @GetMapping("/{pin}/init")
    public ResponseEntity<?> setUpParty(@PathVariable String pin) throws SQLException {
        DBControler db = new DBControler();
        Connection conn = db.connectDB();

        List<String> players = getPlayers(pin, conn);

        if (players == null || players.isEmpty()) {
            conn.close();
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "PIN_NOT_FOUND",
                            "message", "No se ha encontrado partida con ese PIN"
                    )
            );
        }

        int rounds = getTotalRounds(pin, conn);
        int choiceTime = getChoiceTime(pin, conn);
        tiktokExtractor.clearVideosFolder();
        Map<String, String> questions = getQuestions(players, rounds, conn);
        conn.close();

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("players", players);
        resultado.put("totalRounds", rounds);
        resultado.put("choiceTime", choiceTime);
        resultado.put("questions", questions);

        partidasListas.put(pin, resultado); // ← guarda antes del return

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{pin}/status")
    public ResponseEntity<?> getPartidaLista(@PathVariable String pin) {
        Map<String, Object> resultado = partidasListas.get(pin);
        if (resultado == null) {
            return ResponseEntity.status(404).body(Map.of("status", "loading"));
        }
        return ResponseEntity.ok(resultado);
    }

    //Funciones :

    public List<String> getPlayers(String pin, Connection conn) throws SQLException {
        UsersPartyDB usersPartyDB = new UsersPartyDB(conn);
        return usersPartyDB.verListaUsuarios(pin);
    }

    public Map<String, String> getQuestions(List<String> players, int rounds, Connection conn) throws SQLException {

        Map<String, String> questions = new LinkedHashMap<>();
        VideosDB videosDB = new VideosDB(conn);
        Set<String> usedUrls = Collections.synchronizedSet(new HashSet<>());

        // 1. Asigna URLs de TikTok a cada ronda
        Map<Integer, String[]> asignaciones = new LinkedHashMap<>();

        for (int ronda = 1; ronda <= rounds; ronda++) {
            List<String> shuffledPlayers = new ArrayList<>(players);
            Collections.shuffle(shuffledPlayers);
            boolean asignada = false;

            // Intenta primero con cada usuario en orden aleatorio
            for (String username : shuffledPlayers) {
                String videoUrl = videosDB.getRandomVideo(username, usedUrls);
                if (videoUrl == null) continue; // este usuario no tiene más videos sin usar

                usedUrls.add(videoUrl);
                asignaciones.put(ronda, new String[]{username, videoUrl});
                asignada = true;
                break;
            }

            // Si todos los usuarios se quedaron sin videos únicos, busca en TODOS sin restricción
            if (!asignada) {
                for (String username : shuffledPlayers) {
                    String videoUrl = videosDB.getRandomVideo(username, new HashSet<>());
                    if (videoUrl == null) continue;

                    asignaciones.put(ronda, new String[]{username, videoUrl});
                    asignada = true;
                    System.out.println("⚠️ Ronda " + ronda + " sin videos nuevos, usando repetido");
                    break;
                }
            }

            if (!asignada) {
                System.err.println("⚠️ Ronda " + ronda + " sin video disponible en absoluto");
            }
        }

        // 2. Descarga todos los videos en paralelo
        List<Thread> hilos = new ArrayList<>();
        Map<Integer, String> resultados = Collections.synchronizedMap(new LinkedHashMap<>());

        for (Map.Entry<Integer, String[]> entry : asignaciones.entrySet()) {
            int ronda = entry.getKey();
            String username = entry.getValue()[0];
            String videoUrl = entry.getValue()[1];

            Thread hilo = new Thread(() -> {
                if (videoUrl.contains("/video/")) {
                    String directUrl = tiktokExtractor.downloadVideo(videoUrl, username, ronda);
                    if (directUrl != null) {
                        resultados.put(ronda, "ronda_" + ronda + "_" + username + "§" + directUrl);
                        System.out.println("✅ Ronda " + ronda + " → " + username + " → " + directUrl);
                    }
                } else {
                    resultados.put(ronda, "ronda_" + ronda + "_" + username + "§" + videoUrl);
                }
            });

            hilos.add(hilo);
            hilo.start();
        }

        // 3. Espera a que todos terminen
        for (Thread hilo : hilos) {
            try { hilo.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // 4. Reconstruye el mapa en orden
        for (int ronda = 1; ronda <= rounds; ronda++) {
            String valor = resultados.get(ronda);
            if (valor != null) {
                String[] partes = valor.split("§");
                questions.put(partes[0], partes[1]);
            }
        }

        return questions;
    }

    public int getTotalRounds(String pin, Connection conn) throws SQLException {
        ConfPartidaDB confPartidaDB = new ConfPartidaDB(conn);
        return confPartidaDB.getTotalRounds(pin);
    }

    public int getChoiceTime(String pin, Connection conn) throws SQLException {
        ConfPartidaDB confPartidaDB = new ConfPartidaDB(conn);
        return confPartidaDB.getChoiceTime(pin);
    }


}