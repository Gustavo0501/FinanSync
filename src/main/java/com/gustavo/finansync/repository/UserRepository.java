package com.gustavo.finansync.repository;

import com.gustavo.finansync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações CRUD da entidade User
 * Extends JpaRepository que fornece métodos básicos como save, findAll, deleteById, etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuário por email (usado no login e validação de email único)
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe usuário com determinado email (validação de duplicatas)
     * @param email Email a ser verificado
     * @return true se existir, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuário por Google ID (usado na integração OAuth)
     * @param googleId ID único do Google
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByGoogleId(String googleId);

    /**
     * Busca usuários ativos (para listagens administrativas futuras)
     * @param isActive Status de ativo/inativo
     * @return Lista de usuários com o status especificado
     */
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive ORDER BY u.createdAt DESC")
    java.util.List<User> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Conta total de usuários ativos (para métricas)
     * @return Número de usuários ativos
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
}
