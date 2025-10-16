package com.gustavo.finansync.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Transaction - Representa as transações financeiras
 */
@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "Valor é obrigatório")
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @NotNull(message = "Data da transação é obrigatória")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private TransactionSource source = TransactionSource.MANUAL;

    @Column(name = "email_message_id")
    private String emailMessageId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 255, message = "Categoria deve ter no máximo 255 caracteres")
    @Column(name = "category", nullable = true)
    private String category;

    // Construtores
    public Transaction() {}

    public Transaction(String description, BigDecimal amount, TransactionType type,
                       LocalDate transactionDate, User user, String category) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
        this.user = user;
        this.category = category;
    }

    @AssertTrue(message = "Receitas devem ser positivas e despesas negativas")
    public boolean isAmountValid() {
        if (type == TransactionType.RECEITA) return amount.compareTo(BigDecimal.ZERO) > 0;
        if (type == TransactionType.DESPESA) return amount.compareTo(BigDecimal.ZERO) < 0;
        return true;
    }


    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public TransactionSource getSource() { return source; }
    public void setSource(TransactionSource source) { this.source = source; }

    public String getEmailMessageId() { return emailMessageId; }
    public void setEmailMessageId(String emailMessageId) { this.emailMessageId = emailMessageId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}