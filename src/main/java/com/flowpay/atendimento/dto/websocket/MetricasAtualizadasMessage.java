package com.flowpay.atendimento.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricasAtualizadasMessage {

    private int totalAtendimentosAtivos;
    private int totalNaFila;
    private int totalAtendentes;
    private int atendentesDisponiveis;
}
