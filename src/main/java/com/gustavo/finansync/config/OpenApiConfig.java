package com.gustavo.finansync.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para documentação OpenAPI/Swagger
 */
@Configuration
public class OpenApiConfig {

    @Value("${finansync.app.version:1.0.0}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinanSync API")
                        .version(appVersion)
                        .description("Sistema de Controle Financeiro Pessoal com Integração Gmail\n\n" +
                                "Esta API permite:\n" +
                                "• Gerenciamento de usuários e autenticação\n" +
                                "• CRUD completo de transações financeiras\n" +
                                "• Categorização automática e personalizada\n" +
                                "• Integração com Gmail para importação automática\n" +
                                "• Dashboard com métricas e gráficos")
                        .contact(new Contact()
                                .name("FinanSync Team")
                                .email("suporte@finansync.com")
                                .url("https://github.com/Gustavo0501/FinanSync"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}