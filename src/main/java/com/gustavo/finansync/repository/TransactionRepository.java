package com.gustavo.finansync.repository;

import com.gustavo.finansync.entity.Transaction;
import com.gustavo.finansync.entity.TransactionType;
import com.gustavo.finansync.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações CRUD da entidade Transaction
 * Inclui consultas complexas para relatórios e dashboard
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUser(Long id, User user);


    /**
     * Busca transações de um usuário com paginação
     * @param user Usuário proprietário
     * @param pageable Configuração de paginação e ordenação
     * @return Página de transações
     */
    Page<Transaction> findByUserOrderByTransactionDateDesc(User user, Pageable pageable);

    /**
     * Busca transações por usuário e tipo (RECEITA/DESPESA)
     * @param user Usuário proprietário
     * @param type Tipo da transação
     * @param pageable Configuração de paginação
     * @return Página de transações filtradas por tipo
     */
    Page<Transaction> findByUserAndTypeOrderByTransactionDateDesc(
            User user, TransactionType type, Pageable pageable);

    /**
     * Busca transações por período
     * @param user Usuário proprietário
     * @param startDate Data inicial
     * @param endDate Data final
     * @param pageable Configuração de paginação
     * @return Transações do período especificado
     */
    Page<Transaction> findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(
            User user, LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Busca transações por descrição (busca textual)
     * @param user Usuário proprietário
     * @param description Texto a ser buscado na descrição
     * @param pageable Configuração de paginação
     * @return Transações que contenham o texto na descrição
     */
    Page<Transaction> findByUserAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(
            User user, String description, Pageable pageable);

    /**
     * Calcula soma total por tipo e período (para dashboard)
     * @param user Usuário proprietário
     * @param type Tipo da transação (RECEITA/DESPESA)
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Soma total das transações
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user = :user AND t.type = :type AND " +
            "t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserAndTypeAndDateBetween(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /*
    /**
     * Agrupa transações por categoria para gráficos
     * @param user Usuário proprietário
     * @param type Tipo da transação
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Lista com categoria e soma por categoria
     */
    /*@Query("SELECT c, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "JOIN t.category c " +
            "WHERE t.user = :user AND t.type = :type AND " +
            "t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY c " +
            "ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumAmountByCategoryAndPeriod(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );*/

    /**
     * Verifica se existe transação importada de email específico
     * (evita duplicação na importação)
     * @param emailMessageId ID da mensagem do Gmail
     * @return true se já foi importada
     */
    boolean existsByEmailMessageId(String emailMessageId);

    /**
     * Conta total de transações do usuário
     * @param user Usuário proprietário
     * @return Número total de transações
     */
    long countByUser(User user);

    // Filtrar por usuário, descrição e intervalo de datas
    Page<Transaction> findByUserAndDescriptionContainingIgnoreCaseAndTransactionDateBetweenOrderByTransactionDateDesc(
            User user, String description, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Filtrar por usuário e data >= início
    Page<Transaction> findByUserAndTransactionDateGreaterThanEqualOrderByTransactionDateDesc(
            User user, LocalDate startDate, Pageable pageable);

    // Filtrar por usuário, descrição e data >= início
    Page<Transaction> findByUserAndDescriptionContainingIgnoreCaseAndTransactionDateGreaterThanEqualOrderByTransactionDateDesc(
            User user, String description, LocalDate startDate, Pageable pageable);

    // Filtrar por usuário e data <= fim
    Page<Transaction> findByUserAndTransactionDateLessThanEqualOrderByTransactionDateDesc(
            User user, LocalDate endDate, Pageable pageable);

    // Filtrar por usuário, descrição e data <= fim
    Page<Transaction> findByUserAndDescriptionContainingIgnoreCaseAndTransactionDateLessThanEqualOrderByTransactionDateDesc(
            User user, String description, LocalDate endDate, Pageable pageable);

}
