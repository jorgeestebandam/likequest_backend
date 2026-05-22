package com.tiktokgame.likequest_backend.controler.deleteParty;

import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.exceptions.PartyNotFound;
import com.tiktokgame.likequest_backend.models.databse.PartidaDB;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/party")
public class DeletePartyController {
    @DeleteMapping("/{pin}")
    public ResponseEntity<?> deleteParty(@PathVariable String pin) {
        try {

            Connection conexion = conectarDB();
            borrarPartida(conexion, pin);
            cerrarConexion(conexion);

        } catch (PartyNotFound e) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "PIN_NOT_FOUND",
                            "message", "No se ha encontrado partida con ese PIN"
                    )
            );
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(
                    Map.of(
                            "error", "DB_ERROR",
                            "message", e.getMessage()
                    )
            );
        }
        return ResponseEntity.ok(
                Map.of("message", "Partida eliminada") // <- JSON
        );

    }


    public Connection conectarDB() throws SQLException {
        DBControler db = new DBControler();
        Connection conexion = db.connectDB();
        return conexion;
    }

    public void borrarPartida(Connection conexion, String pin) throws SQLException, PartyNotFound {
        PartidaDB partidaDB = new PartidaDB(conexion);
        boolean exist = partidaDB.borrarPartida(pin);
        if (!exist) {
            throw new PartyNotFound("PARTIDA NO ENCONTRADA");
        }
    }

    public void cerrarConexion(Connection conexion) throws SQLException {
        conexion.close();
    }
}
