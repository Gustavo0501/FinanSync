package com.gustavo.finansync.controller;

import com.gustavo.finansync.service.GmailAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/gmail")
public class GmailController {

    @Autowired
    private GmailAuthService gmailAuthService;

    @GetMapping("/authorize-url")
    public ResponseEntity<String> authorizeUrl(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }
        try {
            String url = gmailAuthService.getAuthorizationUrl(principal.getName());
            return ResponseEntity.ok(url); // text/plain
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao gerar URL de autorização");
        }
    }

    @GetMapping("/oauth2callback")
    public org.springframework.web.servlet.view.RedirectView oauth2Callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) {
        try {
            gmailAuthService.exchangeCodeForToken(code, state);
            // Redireciona para o seu frontend (ajuste a URL se necessário)
            return new RedirectView("http://localhost:5173/dashboard?gmail=connected");
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView("http://localhost:5173/dashboard?gmail=error");
        }
    }

}
