package com.gustavo.finansync.dto;

import com.gustavo.finansync.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO(
        Long id,
        String description,
        String category,
        BigDecimal amount,
        LocalDate transactionDate,
        TransactionType type
) {}
