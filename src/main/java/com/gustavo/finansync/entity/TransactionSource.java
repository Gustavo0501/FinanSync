package com.gustavo.finansync.entity;

/**
 * Enum para origem da transação
 */
public enum TransactionSource {
    MANUAL("Manual"),
    EMAIL_IMPORT("Importação por Email");

    private final String displayName;

    TransactionSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}