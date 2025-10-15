package com.gustavo.finansync.controller;

import com.gustavo.finansync.dto.response.ApiResponse;
import com.gustavo.finansync.dto.response.HealthResponse;
import com.gustavo.finansync.repository.UserRepository;
import com.gustavo.finansync.repository.TransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Controller para verificação de saúde da aplicação
 * Primeiro controller para testar se a API está funcionando
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Endpoints para verificação de saúde da aplicação")
public class HealthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DataSource dataSource;

    @Value("${finansync.app.version:1.0.0}")
    private String appVersion;

    /**
     * Endpoint simples para verificar se a API está funcionando
     * @return Resposta básica com status da aplicação
     */
    @GetMapping
    @Operation(summary = "Verificação básica de saúde",
            description = "Endpoint simples para verificar se a API está respondendo")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(
                ApiResponse.success("FinanSync API está funcionando corretamente!")
        );
    }

    /**
     * Endpoint detalhado com informações sobre banco de dados e métricas
     * @return Informações detalhadas sobre o status da aplicação
     */
    @GetMapping("/detailed")
    @Operation(summary = "Verificação detalhada de saúde",
            description = "Informações detalhadas sobre status da aplicação e banco de dados")
    public ResponseEntity<ApiResponse<HealthResponse>> detailedHealthCheck() {
        try {
            // Criar resposta base
            HealthResponse response = new HealthResponse(
                    "UP",
                    "FinanSync API está operacional",
                    appVersion
            );

            // Verificar conexão com banco de dados
            HealthResponse.DatabaseInfo dbInfo = checkDatabaseConnection();
            response.setDatabase(dbInfo);

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            HealthResponse errorResponse = new HealthResponse(
                    "DOWN",
                    "Erro na verificação de saúde: " + e.getMessage(),
                    appVersion
            );

            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Erro interno do servidor", errorResponse));
        }
    }

    /**
     * Endpoint específico para testar conexão com banco de dados
     * @return Status da conexão com o banco
     */
    @GetMapping("/database")
    @Operation(summary = "Verificar conexão com banco de dados",
            description = "Testa especificamente a conectividade com o MySQL")
    public ResponseEntity<ApiResponse<HealthResponse.DatabaseInfo>> databaseHealthCheck() {
        try {
            HealthResponse.DatabaseInfo dbInfo = checkDatabaseConnection();
            return ResponseEntity.ok(ApiResponse.success(
                    "Conexão com banco de dados verificada com sucesso", dbInfo));

        } catch (Exception e) {
            HealthResponse.DatabaseInfo errorInfo = new HealthResponse.DatabaseInfo(
                    "ERROR", 0, 0);

            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Erro na conexão com banco: " + e.getMessage(), errorInfo));
        }
    }

    /**
     * Método privado para verificar conexão e obter métricas do banco
     * @return Informações sobre o status do banco de dados
     */
    private HealthResponse.DatabaseInfo checkDatabaseConnection() {
        try {
            // Testar conexão
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) { // timeout de 5 segundos
                    // Obter métricas básicas
                    long totalUsers = userRepository.count();
                    long totalTransactions = transactionRepository.count();

                    return new HealthResponse.DatabaseInfo(
                            "CONNECTED", totalUsers, totalTransactions);
                } else {
                    return new HealthResponse.DatabaseInfo("INVALID_CONNECTION", 0, 0);
                }
            }
        } catch (Exception e) {
            return new HealthResponse.DatabaseInfo("CONNECTION_ERROR", 0, 0);
        }
    }
}
