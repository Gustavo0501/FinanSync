package com.gustavo.finansync.controller;

import com.google.api.services.gmail.Gmail;
import com.gustavo.finansync.dto.TransactionDTO;
import com.gustavo.finansync.entity.User;
import com.gustavo.finansync.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions/import")
public class TransactionImportController {

    private final GmailService gmailService;
    private final CsvParsingService csvParsingService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final GmailAuthService gmailAuthService;

    public TransactionImportController(GmailService gmailService, CsvParsingService csvParsingService, UserService userService,
                                       TransactionService transactionService, GmailAuthService gmailAuthService) {
        this.gmailService = gmailService;
        this.csvParsingService = csvParsingService;
        this.userService = userService;
        this.transactionService = transactionService;
        this.gmailAuthService = gmailAuthService;
    }

    @GetMapping("/analyze")
    public List<TransactionDTO> analyze(
            @RequestParam String remetente,
            @RequestParam String assunto,
            Authentication authentication
    ) throws Exception {
        String userEmail = authentication.getName();
        // Obtenha o Gmail autenticado para o usuário
        Gmail gmail = gmailAuthService.getGmailService(userEmail);

        // Busque os anexos usando o Gmail autenticado
        List<InputStream> anexos = gmailService.buscarAnexosCsv(gmail, "me", remetente, assunto);

        List<TransactionDTO> allTransactions = new ArrayList<>();
        for (InputStream csv : anexos) {
            allTransactions.addAll(csvParsingService.parseCsv(csv));
        }

        return allTransactions;
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmImport(@RequestBody List<TransactionDTO> transactions, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userService.findByEmail(userEmail);
        transactionService.saveImportedTransactions(transactions, user);
        return ResponseEntity.ok().build();
    }
}
