package com.tiktokgame.likequest_backend.controler.joinParty;

import com.tiktokgame.likequest_backend.controler.database.DBControler;
import com.tiktokgame.likequest_backend.exceptions.*;
import com.tiktokgame.likequest_backend.models.User;
import com.tiktokgame.likequest_backend.models.databse.*;
import com.tiktokgame.likequest_backend.models.joinParty.JoinPartyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/party")
public class JoinPartyController {
    @PostMapping("/join")
    public ResponseEntity<?> joinParty(@RequestBody JoinPartyRequest request) {
        try {
            Connection conexion = conectarDB();
            validarPartidaExistente(conexion, request);
            String estado = validarEstadoPartida(conexion, request);
            if (estado.equals("jugando")) {
                validarJoinLater(conexion, request);
            }
            gestionUsers(conexion, request.getUser());
            // comprobar que cuando se haga la configuracion si quiere cambiar el numero de jugadores pero ya
            // son muchos alertar con exception de que no se puede
            validarDuplicidadEnTablaUsersParty(conexion, request);
            validarMaximoJugadoresPartida(conexion, request);
            insertarJugadorEnPartida(conexion, request);
            cerrarConexion(conexion);


            return ResponseEntity.ok(
                    Map.of(
                            "message", "Usuario unido correctamente"
                    )
            );


        } catch (PartyNotFound e) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "PIN_NOT_FOUND",
                            "message", "No se ha encontrado partida con ese PIN"
                    )
            );

        } catch (PartyFinishedException e) {
            return ResponseEntity.status(409).body(
                    Map.of(
                            "error", "PARTY_FINISHED",
                            "message", "La partida ha finalizado"
                    ));
        } catch (LateJoinNotAllowedException e) {
            return ResponseEntity.status(403).body(
                    Map.of(
                            "error", "JOIN_LATER_NOT_ALLOWED",
                            "message", "La partida esta en curso , pero no esta permitido unirse mas tarde"
                    ));
        } catch (UserAlreadyInPartyException e) {
            return ResponseEntity.status(405).body(
                    Map.of(
                            "error", "USER_ALREADY_IN_PARTY",
                            "message", "Ya existe ese usuario en la partida"
                    ));
        } catch (MaxPlayerReachedException e) {
            return ResponseEntity.status(406).body(
                    Map.of(
                            "error", "LIMIT_USERS_REACHED",
                            "message", "Ya se alcanzo el limite de jugadores de esta partida"
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

    public boolean validarPartidaExistente(Connection conexion, JoinPartyRequest request) throws PartyNotFound, SQLException {

        boolean existe = false;
        String pin = request.getPin();
        PartidaDB partida = new PartidaDB(conexion);

        existe = partida.existPin(pin);

        if (!existe) {
            throw new PartyNotFound(pin);
        }
        return existe;
    }

    public String validarEstadoPartida(Connection conexion, JoinPartyRequest request) throws PartyFinishedException, SQLException {
        PartidaDB partida = new PartidaDB(conexion);
        String estado;

        estado = partida.validarEstado(request.getPin());

        if (estado == null) {
            throw new PartyFinishedException(request.getPin());
        }
        return estado;
    }

    public boolean validarJoinLater(Connection conexion, JoinPartyRequest request) throws LateJoinNotAllowedException, SQLException {
        boolean joinLater = false;
        String pin = request.getPin();
        ConfPartidaDB confPartidaDB = new ConfPartidaDB(conexion);

        joinLater = confPartidaDB.validarJoinLater(pin);


        if (!joinLater) {
            throw new LateJoinNotAllowedException(request.getPin());
        }

        return joinLater;
    }

    public void gestionUsers(Connection conexion, User user) throws SQLException {
        UserDB userDB = new UserDB(conexion, user);
        boolean insertado = userDB.insertUser();
        VideosDB videosDB = new VideosDB(conexion, user);
        if (insertado) {
            videosDB.insertVideos();
        } else {
            videosDB.updateVideos();
        }
    }

    public void validarDuplicidadEnTablaUsersParty(Connection conexion, JoinPartyRequest request) throws UserAlreadyInPartyException, SQLException {
        boolean duplicidad = false;
        String pin = request.getPin();
        User user = request.getUser();
        UsersPartyDB usersPartyDB = new UsersPartyDB(conexion);

        duplicidad = usersPartyDB.duplicidadUsuario(user.getUsername(), pin);


        if (duplicidad) {
            throw new UserAlreadyInPartyException(user.getUsername(), pin);
        }
    }

    public void validarMaximoJugadoresPartida(Connection conexion, JoinPartyRequest request) throws SQLException, MaxPlayerReachedException {

        String pin = request.getPin();
        String username = request.getUser().getUsername();
        UsersPartyDB usersPartyDB = new UsersPartyDB(conexion);
        List<String> listadoJugadores = usersPartyDB.verListaUsuarios(pin);
        ConfPartidaDB confPartidaDB = new ConfPartidaDB(conexion);
        int maxUsers = confPartidaDB.obtenerMaxUsers(pin);

        if (listadoJugadores.size() >= maxUsers) {
            throw new MaxPlayerReachedException(pin);
        }
    }

    public void insertarJugadorEnPartida(Connection conexion, JoinPartyRequest request) throws SQLException {
        String pin = request.getPin();
        String username = request.getUser().getUsername();
        UsersPartyDB usersPartyDB = new UsersPartyDB(conexion);
        usersPartyDB.agregarUsuario(username, pin);
    }

    public void cerrarConexion(Connection conexion) throws SQLException {
        conexion.close();
    }
}
