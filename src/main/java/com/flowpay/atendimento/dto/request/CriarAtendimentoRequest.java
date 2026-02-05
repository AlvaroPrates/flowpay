package com.flowpay.atendimento.dto.request;

import com.flowpay.atendimento.model.Time;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de novo atendimento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de um novo atendimento")
public class CriarAtendimentoRequest {

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Schema(
        description = "Nome completo do cliente",
        example = "João Silva",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nomeCliente;

    @NotBlank(message = "Assunto é obrigatório")
    @Schema(
        description = "Assunto/motivo do atendimento",
        example = "Problemas com cartão de crédito",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String assunto;

    @NotNull(message = "Time é obrigatório")
    @Schema(
        description = "Time responsável pelo atendimento",
        example = "CARTOES",
        allowableValues = {"CARTOES", "EMPRESTIMOS", "OUTROS"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Time time;
}
