package com.gustavo.finansync.repository;

import com.gustavo.finansync.entity.Category;
import com.gustavo.finansync.entity.TransactionType;
import com.gustavo.finansync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações CRUD da entidade Category
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Busca categorias de um usuário específico
     * @param user Usuário proprietário das categorias
     * @return Lista de categorias do usuário ordenadas por nome
     */
    List<Category> findByUserOrderByName(User user);

    /**
     * Busca categorias de um usuário por tipo (RECEITA ou DESPESA)
     * @param user Usuário proprietário
     * @param type Tipo da transação
     * @return Lista de categorias filtradas por tipo
     */
    List<Category> findByUserAndTypeOrderByName(User user, TransactionType type);

    /**
     * Busca categoria por nome e usuário (para evitar duplicatas)
     * @param name Nome da categoria
     * @param user Usuário proprietário
     * @return Optional contendo a categoria se encontrada
     */
    Optional<Category> findByNameAndUser(String name, User user);

    /**
     * Verifica se existe categoria com determinado nome para o usuário
     * @param name Nome da categoria
     * @param user Usuário proprietário
     * @return true se existir, false caso contrário
     */
    boolean existsByNameAndUser(String name, User user);

    /**
     * Busca categorias padrão do sistema (is_default = true)
     * @return Lista de categorias padrão
     */
    List<Category> findByIsDefaultTrueOrderByName();

    /**
     * Conta quantas transações usam uma categoria específica
     * (usado antes de deletar uma categoria)
     * @param categoryId ID da categoria
     * @return Número de transações que usam a categoria
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.category.id = :categoryId")
    long countTransactionsByCategory(@Param("categoryId") Long categoryId);
}