package com.flowpay.atendimento.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do SpringDoc OpenAPI (Swagger).
 *
 * Acesse a documentação em:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flowPayOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Servidor Local de Desenvolvimento");

        Contact contact = new Contact();
        contact.setName("FlowPay - Equipe de Desenvolvimento");
        contact.setEmail("dev@flowpay.com.br");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("FlowPay - API de Distribuição de Atendimentos")
                .version("1.0.0")
                .description("API REST para gerenciamento e distribuição automática de atendimentos " +
                            "em uma central de relacionamento. O sistema distribui atendimentos para " +
                            "equipes especializadas (Cartões, Empréstimos e Outros Assuntos), " +
                            "respeitando limite de 3 atendimentos simultâneos por atendente.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
