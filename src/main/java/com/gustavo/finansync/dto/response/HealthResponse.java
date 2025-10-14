package com.gustavo.finansync.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO de resposta para verificação de saúde da aplicação
 */
public class HealthResponse {

    private String status;
    private String message;
    private String version;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private DatabaseInfo database;

    // Construtores
    public HealthResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public HealthResponse(String status, String message, String version) {
        this();
        this.status = status;
        this.message = message;
        this.version = version;
    }

    // Getters e Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public DatabaseInfo getDatabase() { return database; }
    public void setDatabase(DatabaseInfo database) { this.database = database; }

    // Classe interna para informações do banco
    public static class DatabaseInfo {
        private String status;
        private long totalUsers;
        private long totalTransactions;

        public DatabaseInfo() {}

        public DatabaseInfo(String status, long totalUsers, long totalTransactions) {
            this.status = status;
            this.totalUsers = totalUsers;
            this.totalTransactions = totalTransactions;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

        public long getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(long totalTransactions) { this.totalTransactions = totalTransactions; }
    }
}