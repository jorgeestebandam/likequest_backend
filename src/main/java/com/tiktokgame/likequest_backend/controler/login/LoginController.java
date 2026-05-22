package com.tiktokgame.likequest_backend.controler.login;

import com.tiktokgame.likequest_backend.exceptions.ProfileIsPrivate;
import com.tiktokgame.likequest_backend.exceptions.ProfileNotFoundException;
import com.tiktokgame.likequest_backend.exceptions.VideosNotPublicException;
import com.tiktokgame.likequest_backend.models.login.LoginResult;
import com.tiktokgame.likequest_backend.models.login.LoginRequest;
import com.tiktokgame.likequest_backend.services.TikTokSeleniumService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api")
public class LoginController {

    private final TikTokSeleniumService service;


    public LoginController(TikTokSeleniumService service) {
        this.service = service;

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("2. Llamando a Selenium...");
            LoginResult result = service.login(request.getUsername());
            if(result.getIsPublic().equals("true") && !result.getListVideosLiked().isEmpty()) {
               //agregar bd
            }

            System.out.println("3. Selenium terminó con éxito: " + result.toString());
            return ResponseEntity.ok(result);

        } catch (VideosNotPublicException e) {
            System.out.println("Videos vacíos/privados para: " + request.getUsername());
            // Devolvemos un objeto LoginResult real, pero con la lista vacía
            // Esto hará que en Flutter 'isVideoPublic' sea calculado como FALSE
            LoginResult errorResult = new LoginResult(
                    request.getUsername(),
                    "true", // El perfil es público
                    new java.util.ArrayList<>() // PERO la lista de videos está VACÍA
            );
            return ResponseEntity.ok(errorResult);

        } catch (ProfileIsPrivate e) {
            // Para perfil privado hacemos lo mismo pero con isPublic "false"
            LoginResult errorResult = new LoginResult(request.getUsername(), "false", new java.util.ArrayList<>());
            return ResponseEntity.ok(errorResult);

        } catch (ProfileNotFoundException e) {
            // Para cuenta no encontrada, sí podemos mantener el 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("message", "EL USUARIO NO EXISTE"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("message", "ERROR CRÍTICO: " + e.getMessage()));
        }
    }

    // Método auxiliar para que el JSON de error sea compatible con tu Flutter
    private ResponseEntity<?> crearErrorResponse(String mensaje, String code, boolean isPublic, boolean isVideoPublic) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("error", code);
        response.put("message", mensaje);
        response.put("isPublic", isPublic);
        response.put("isVideoPublic", isVideoPublic);

        // Usamos 200 aunque sea "error" de negocio para que Flutter entre en el bloque
        // donde lee los booleanos, o puedes usar 400 y ajustar Flutter.
        return ResponseEntity.ok(response);
    }
}
