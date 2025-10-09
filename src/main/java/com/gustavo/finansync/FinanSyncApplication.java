package com.gustavo.finansync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Classe principal da aplicação FinanSync
 * Sistema de Controle Financeiro Pessoal com Integração Gmail
 *
 * @author FinanSync Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class FinanSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanSyncApplication.class, args);
        System.out.println("🚀 FinanSync Backend iniciado com sucesso!");
        System.out.println("📊 Swagger UI: http://localhost:8080/api/swagger-ui.html");
        System.out.println("🔗 API Docs: http://localhost:8080/api/api-docs");
    }
}
