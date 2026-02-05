package com.flowpay.atendimento.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estrutura padronizada de resposta de erro.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta padronizada de erro da API")
public class ErrorResponse {

    @Schema(description = "Data e hora do erro", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", example = "404")
    private int status;

    @Schema(description = "Nome do erro HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Mensagem descritiva do erro", example = "Atendimento não encontrado: 123")
    private String message;

    @Schema(description = "Caminho da requisição que gerou o erro", example = "/api/atendimentos/123")
    private String path;

    @Schema(description = "Detalhes adicionais sobre o erro")
    private List<String> detalhes;
}
