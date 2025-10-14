package com.gustavo.finansync.dto;

public class AuthDTOs {
    // Requisição para registro de usuário
    public record UserRegisterRequest(String name, String email, String password) {}

    // Requisição para login
    public record UserLoginRequest(String email, String password) {}

    // Resposta com o token JWT
    public record AuthResponse(String token) {}
}
