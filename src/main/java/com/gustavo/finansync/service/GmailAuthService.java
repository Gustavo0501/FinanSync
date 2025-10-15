package com.gustavo.finansync.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GmailAuthService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final Logger logger = LoggerFactory.getLogger(GmailAuthService.class);

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private AuthorizationCodeFlow flow;
    private NetHttpTransport httpTransport;

    @PostConstruct
    public void init() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // CORREÇÃO PRINCIPAL ESTÁ AQUI:
            // Construímos o GoogleClientSecrets programaticamente em vez de ler de um arquivo.
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            // O URI de autenticação e token são padrões do Google, não precisam ser setados geralmente.

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);

            List<String> scopes = Collections.singletonList(GmailScopes.GMAIL_READONLY);

            flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, scopes)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .setApprovalPrompt("force") // Garante que o refresh_token seja enviado sempre
                    .build();

            logger.info("Fluxo de autorização do Gmail inicializado com sucesso.");

        } catch (GeneralSecurityException | IOException e) {
            logger.error("Falha ao inicializar o GmailAuthService", e);
            // Lança uma exceção para impedir que a aplicação suba com configuração inválida
            throw new RuntimeException("Não foi possível inicializar o serviço de autenticação do Gmail", e);
        }
    }

    public String getAuthorizationUrl(String userId) throws IOException {
        String redirectUri = "http://localhost:8080/api/gmail/oauth2callback";
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(userId) // Usamos o ID do usuário para associar o callback
                .build();
    }

    public void exchangeCodeForToken(String code, String userId) throws IOException {
        String redirectUri = "http://localhost:8080/api/gmail/oauth2callback";
        flow.createAndStoreCredential(
                flow.newTokenRequest(code).setRedirectUri(redirectUri).execute(),
                userId
        );
        logger.info("Token para o usuário '{}' armazenado com sucesso.", userId);
    }

    public Credential getCredential(String userId) throws IOException {
        return flow.loadCredential(userId);
    }

    public Gmail getGmailService(String userId) throws IOException, GeneralSecurityException {
        Credential credential = getCredential(userId);
        if (credential == null || (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60)) {
            // Se a credencial não existir ou estiver prestes a expirar, o fluxo de autorização é necessário.
            throw new IOException("Credencial do Gmail inválida ou expirada. Por favor, autorize novamente.");
        }

        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("FinanSync")
                .build();
    }
}
