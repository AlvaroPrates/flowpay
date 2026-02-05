package com.flowpay.atendimento.dto.request;

import com.flowpay.atendimento.model.Time;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para cadastro de um novo atendente")
public class CadastrarAtendenteRequest {

    @NotBlank(message = "Nome do atendente é obrigatório")
    @Schema(
        description = "Nome completo do atendente",
        example = "Ana Silva",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @NotNull(message = "Time é obrigatório")
    @Schema(
        description = "Time ao qual o atendente pertence",
        example = "CARTOES",
        allowableValues = {"CARTOES", "EMPRESTIMOS", "OUTROS"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Time time;
}
