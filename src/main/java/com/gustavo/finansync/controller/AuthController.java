package com.gustavo.finansync.controller;

import com.gustavo.finansync.dto.AuthDTOs;
import com.gustavo.finansync.entity.User;
import com.gustavo.finansync.service.JwtTokenService;
import com.gustavo.finansync.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    public AuthController(UserService userService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthDTOs.UserRegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTOs.AuthResponse> login(@RequestBody AuthDTOs.UserLoginRequest request) {
        try {
            User usuario = userService.authenticate(request.email(), request.password());
            String token = jwtTokenService.generateToken(usuario);
            return ResponseEntity.ok(new AuthDTOs.AuthResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}

