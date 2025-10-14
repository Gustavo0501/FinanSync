package com.gustavo.finansync.service;

import com.gustavo.finansync.dto.TransactionDTO;
import com.gustavo.finansync.entity.Transaction;
import com.gustavo.finansync.entity.User;
import com.gustavo.finansync.repository.TransactionRepository;
import com.gustavo.finansync.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionDTO create(TransactionDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Transaction transaction = new Transaction();
        // Mapeamento do DTO para a entidade
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setType(dto.type());
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return toDTO(savedTransaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO> findAll(String description, int page, int size, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

        Page<Transaction> transactionPage;
        if (description != null && !description.isEmpty()) {
            transactionPage = transactionRepository.findByUserAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(user, description, pageable);
        } else {
            transactionPage = transactionRepository.findAll(pageable);
        }

        return transactionPage.map(this::toDTO);
    }

    // NOVO: usado pelo controller passando o User autenticado
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findAllByUser(User user, String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

        Page<Transaction> transactionPage;
        if (description != null && !description.isEmpty()) {
            transactionPage = transactionRepository
                    .findByUserAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(user, description, pageable);
        } else {
            transactionPage = transactionRepository
                    .findByUserOrderByTransactionDateDesc(user, pageable);
        }
        return transactionPage.map(this::toDTO);
    }

    @Transactional
    public TransactionDTO update(Long id, TransactionDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + id));

        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setType(dto.type());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return toDTO(updatedTransaction);
    }

    // NOVO: garante que a transação pertence ao usuário antes de atualizar
    @Transactional
    public TransactionDTO updateForUser(Long id, TransactionDTO dto, User user) {
        Transaction tx = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada para este usuário."));

        tx.setDescription(dto.description());
        tx.setAmount(dto.amount());
        tx.setTransactionDate(dto.transactionDate());
        tx.setType(dto.type());

        Transaction updated = transactionRepository.save(tx);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transação não encontrada com o id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // NOVO: garante que a transação pertence ao usuário antes de deletar
    @Transactional
    public void deleteForUser(Long id, User user) {
        Transaction tx = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada para este usuário."));
        transactionRepository.delete(tx);
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

