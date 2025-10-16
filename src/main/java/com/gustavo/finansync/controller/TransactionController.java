package com.gustavo.finansync.controller;

import com.gustavo.finansync.dto.TransactionDTO;
import com.gustavo.finansync.entity.User;
import com.gustavo.finansync.service.TransactionService;
import com.gustavo.finansync.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto, Authentication authentication) {
        // Supondo que o 'name' do Principal é o email do usuário
        String userEmail = authentication.getName();
        // Você precisará de um método no UserService para buscar por email
        User user = userService.findByEmail(userEmail);

        TransactionDTO createdDto = transactionService.create(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactionDTO>> getAllFilteredTransactions(
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {

        User user = userService.findByEmail(authentication.getName());
        List<TransactionDTO> transactions = transactionService.findAllByUserNoPagination(user, description, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO dto, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        try {
            TransactionDTO updatedDto = transactionService.updateForUser(id, dto, user);
            return ResponseEntity.ok(updatedDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id,Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        try {
            transactionService.deleteForUser(id, user);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        User user = userService.findByEmail(authentication.getName());
        Page<TransactionDTO> transactions = transactionService.findByDateRange(
                user, description, startDate, endDate, page, size
        );
        return ResponseEntity.ok(transactions);
    }

}

