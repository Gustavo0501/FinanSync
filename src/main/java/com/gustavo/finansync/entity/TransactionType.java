package com.gustavo.finansync.entity;

/**
 * Enum para tipos de transação
 */
public enum TransactionType {
    RECEITA("Receita"),
    DESPESA("Despesa");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}