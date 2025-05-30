package com.ali.amara.user;


import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class); // Déclaration du logger
    @Autowired
    private UserService userService; // Ou autre service

    @CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        logger.info("Requête GET /user/me reçue");

        // Récupérer l'authentification actuelle
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentification : {}", authentication);

        // Extraire l'identifiant (e-mail ou téléphone) de l'authentification
        String identifier = authentication.getName();
        logger.info("Identifiant extrait : {}", identifier);

        // Récupérer l'utilisateur à partir de l'identifiant
        UserDTO user = userService.getCurrentUser(identifier); // Utilisez l'identifiant pour récupérer l'utilisateur
        if (user == null) {
            logger.error("Utilisateur non trouvé pour l'identifiant : {}", identifier);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(user);
    }
    @GetMapping("/home")
            public String home() {
        System.out.println("hello");
    return "home";}

}

