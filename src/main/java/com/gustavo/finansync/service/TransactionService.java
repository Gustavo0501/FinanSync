package com.gustavo.finansync.service;

import com.gustavo.finansync.dto.TransactionDTO;
import com.gustavo.finansync.entity.Transaction;
import com.gustavo.finansync.entity.TransactionSource;
import com.gustavo.finansync.entity.User;
import com.gustavo.finansync.repository.TransactionRepository;
import com.gustavo.finansync.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        transaction.setCategory(dto.category());
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
        transaction.setCategory(dto.category());
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
        tx.setCategory(dto.category());
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
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getType()
        );
    }

    @Transactional
    public void saveImportedTransactions(List<TransactionDTO> dtos, User user) {
        for (TransactionDTO dto : dtos) {
            Transaction transaction = new Transaction();
            transaction.setDescription(dto.description());
            transaction.setCategory(dto.category());
            transaction.setAmount(dto.amount());
            transaction.setTransactionDate(dto.transactionDate());
            transaction.setType(dto.type());
            transaction.setUser(user);
            transaction.setSource(TransactionSource.EMAIL_IMPORT); // Marca como importada por email
            transactionRepository.save(transaction);
        }
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO> findByDateRange(
            User user,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactionPage;

        if (startDate != null && endDate != null) {
            if (description != null && !description.isEmpty()) {
                transactionPage = transactionRepository
                        .findByUserAndDescriptionContainingIgnoreCaseAndTransactionDateBetweenOrderByTransactionDateDesc(
                                user, description, startDate, endDate, pageable);
            } else {
                transactionPage = transactionRepository
                        .findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(
                                user, startDate, endDate, pageable);
            }
        } else if (startDate != null) {
            if (description != null && !description.isEmpty()) {
                transactionPage = transactionRepository
                        .findByUserAndDescriptionContainingIgnoreCaseAndTransactionDateGreaterThanEqualOrderByTransactionDateDesc(
                                user, description, startDate, pageable);
            } else {
                transactionPage = transactionRepository
                        .findByUserAndTransactionDateGreaterThanEqualOrderByTransactionDateDesc(
                                user, startDate, pageable);
            }
        } else if (endDate != null) {
            if (description != null && !description.isEmpty()) {
                transactionPage = transactionRepository
                        .findByUserAndDescriptionContainingIgnoreCaseAndTransactionDateLessThanEqualOrderByTransactionDateDesc(
                                user, description, endDate, pageable);
            } else {
                transactionPage = transactionRepository
                        .findByUserAndTransactionDateLessThanEqualOrderByTransactionDateDesc(
                                user, endDate, pageable);
            }
        } else {
            if (description != null && !description.isEmpty()) {
                transactionPage = transactionRepository
                        .findByUserAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(
                                user, description, pageable);
            } else {
                transactionPage = transactionRepository
                        .findByUserOrderByTransactionDateDesc(user, pageable);
            }
        }

        return transactionPage.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> findAllByUserNoPagination(User user, String description, LocalDate startDate, LocalDate endDate) {
        // Usa Specification para criar uma query dinâmica
        Specification<Transaction> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro obrigatório por usuário
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            // Filtro opcional por descrição
            if (description != null && !description.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            // Filtro opcional por data de início
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
            }

            // Filtro opcional por data de fim
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Busca todas as transações que correspondem aos filtros, sem paginação
        List<Transaction> transactions = transactionRepository.findAll(spec);

        // Converte a lista de entidades para uma lista de DTOs
        return transactions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

