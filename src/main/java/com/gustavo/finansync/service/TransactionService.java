package com.gustavo.finansync.service;

import com.gustavo.finansync.dto.TransactionDTO;
import com.gustavo.finansync.entity.Transaction;
import com.gustavo.finansync.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionDTO create(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        // Mapeamento do DTO para a entidade
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setTransactionDate(dto.date());
        transaction.setType(dto.type());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return toDTO(savedTransaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO> findAll(String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Page<Transaction> transactionPage;
        if (description != null && !description.isEmpty()) {
            transactionPage = transactionRepository.findByDescriptionContainingIgnoreCase(description, pageable);
        } else {
            transactionPage = transactionRepository.findAll(pageable);
        }

        return transactionPage.map(this::toDTO);
    }

    @Transactional
    public TransactionDTO update(Long id, TransactionDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + id));

        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setTransactionDate(dto.date());
        transaction.setType(dto.type());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return toDTO(updatedTransaction);
    }

    @Transactional
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transação não encontrada com o id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // Método utilitário para converter Entidade para DTO
    private TransactionDTO toDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getType()
        );
    }
}

