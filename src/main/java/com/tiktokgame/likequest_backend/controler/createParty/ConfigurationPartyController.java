package com.tiktokgame.likequest_backend.controler.createParty;

import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.exceptions.PlayerCountExceedsLimitException;
import com.tiktokgame.likequest_backend.models.databse.ConfPartidaDB;
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
public class ConfigurationPartyController {
    @PutMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Map<String, Object> body) {
        try {
            String pin = (String) body.get("pin");
            int rounds = (int) body.get("number_rounds");
            int time = (int) body.get("choice_time");
            int max = (int) body.get("max_users");
            boolean joinLater = (boolean) body.get("join_later");

            Connection conexion = conectarDB();
            validarJugadoresActualesPartida(conexion, pin, max);
            gestionConfiguaracionPartida(conexion, pin, rounds, time, max, joinLater);
            desconectarDB(conexion);

            return ResponseEntity.ok().build();


        } catch (PlayerCountExceedsLimitException e) {
            return ResponseEntity.status(400).body(
                    Map.of(
                            "error", "PLAYER_LIMIT_EXCEEDED",
                            "message", "No se puede establecer ese limite de jugadores , ya se ha superado "
                    )
            );
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(
                    Map.of(
                            "error", "DB_ERROR",
                            "message", "Error interno de base de datos"
                    )
            );
        }
    }

    public Connection conectarDB() throws SQLException {
        DBControler db = new DBControler();
        Connection conexion = db.connectDB();
        return conexion;
    }

    public void validarJugadoresActualesPartida(Connection conexion, String pin, int max) throws SQLException, PlayerCountExceedsLimitException {
        int jugadores = 0;
        UsersPartyDB usersPartyDB = new UsersPartyDB(conexion);
        List<String> listaJugadores = usersPartyDB.verListaUsuarios(pin);

        if (listaJugadores.size() > max) {
            throw new PlayerCountExceedsLimitException(pin, max);
        }
    }

    public void gestionConfiguaracionPartida
            (Connection conexion, String pin, int rounds, int time, int max, boolean joinLater)
            throws SQLException {
        ConfPartidaDB conf = new ConfPartidaDB(conexion);
        conf.updateConfig(pin, rounds, time, max, joinLater);

    }


    public void desconectarDB(Connection conexion) throws SQLException {
        DBControler db = new DBControler();
        db.closeDBConnection(conexion);
    }

}
