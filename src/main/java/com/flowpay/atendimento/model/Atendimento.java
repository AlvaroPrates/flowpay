package com.flowpay.atendimento.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Atendimento {

    private Long id;
    private Long atendenteId;
    private Time time;
    private String assunto;
    private String nomeCliente;
    private StatusAtendimento status;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraAtendimento;
    private LocalDateTime dataHoraFinalizacao;
}
