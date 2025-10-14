package com.gustavo.finansync.controller;

import com.gustavo.finansync.dto.TransactionDTO;
import com.gustavo.finansync.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto) {
        TransactionDTO createdDto = transactionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TransactionDTO> transactions = transactionService.findAll(description, page, size);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO dto) {
        try {
            TransactionDTO updatedDto = transactionService.update(id, dto);
            return ResponseEntity.ok(updatedDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

