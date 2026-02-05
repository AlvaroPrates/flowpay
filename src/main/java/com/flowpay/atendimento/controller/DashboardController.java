package com.flowpay.atendimento.controller;

import com.flowpay.atendimento.dto.response.AtendenteResponse;
import com.flowpay.atendimento.dto.response.AtendimentoResponse;
import com.flowpay.atendimento.dto.response.DashboardMetricasResponse;
import com.flowpay.atendimento.dto.response.TimeStatusResponse;
import com.flowpay.atendimento.model.StatusAtendimento;
import com.flowpay.atendimento.model.Time;
import com.flowpay.atendimento.service.AtendenteService;
import com.flowpay.atendimento.service.AtendimentoService;
import com.flowpay.atendimento.service.FilaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Dashboard", description = "Métricas e dados para visualização no dashboard")
public class DashboardController {

    private final AtendimentoService atendimentoService;
    private final AtendenteService atendenteService;
    private final FilaService filaService;

    @Operation(
        summary = "Obter métricas gerais",
        description = "Retorna métricas consolidadas do sistema: total de atendimentos ativos, " +
                     "filas, atendentes disponíveis e estatísticas por time"
    )
    @GetMapping("/metricas")
    public ResponseEntity<DashboardMetricasResponse> obterMetricas() {

        int totalAtivos = atendimentoService.listarPorStatus(StatusAtendimento.EM_ATENDIMENTO)
                .size();

        int totalFila = Arrays.stream(Time.values())
                .mapToInt(filaService::tamanhoFila)
                .sum();

        List<com.flowpay.atendimento.model.Atendente> todosAtendentes = atendenteService.listarTodos();
        int totalAtendentes = todosAtendentes.size();
        int atendentesDisponiveis = (int) todosAtendentes.stream()
                .filter(com.flowpay.atendimento.model.Atendente::isDisponivel)
                .count();

        Map<Time, Integer> filasPorTime = new HashMap<>();
        Map<Time, Integer> ativosPorTime = new HashMap<>();

        for (Time time : Time.values()) {
            filasPorTime.put(time, filaService.tamanhoFila(time));

            int ativos = (int) atendimentoService.listarPorTime(time)
                    .stream()
                    .filter(a -> a.getStatus() == StatusAtendimento.EM_ATENDIMENTO)
                    .count();
            ativosPorTime.put(time, ativos);
        }

        DashboardMetricasResponse metricas = DashboardMetricasResponse.builder()
                .totalAtendimentosAtivos(totalAtivos)
                .totalNaFila(totalFila)
                .totalAtendentes(totalAtendentes)
                .atendentesDisponiveis(atendentesDisponiveis)
                .filasPorTime(filasPorTime)
                .atendimentosAtivosPorTime(ativosPorTime)
                .build();

        return ResponseEntity.ok(metricas);
    }

    @Operation(
        summary = "Obter status de um time",
        description = "Retorna informações detalhadas sobre um time específico: " +
                     "atendentes, fila de espera e atendimentos ativos"
    )
    @GetMapping("/time/{time}")
    public ResponseEntity<TimeStatusResponse> obterStatusTime(
            @Parameter(description = "Time", example = "CARTOES")
            @PathVariable Time time) {

        List<AtendenteResponse> atendentes = atendenteService.listarPorTime(time)
                .stream()
                .map(AtendenteResponse::fromEntity)
                .collect(Collectors.toList());

        List<AtendimentoResponse> fila = filaService.listarFila(time)
                .stream()
                .map(AtendimentoResponse::fromEntity)
                .collect(Collectors.toList());

        int ativos = (int) atendimentoService.listarPorTime(time)
                .stream()
                .filter(a -> a.getStatus() == StatusAtendimento.EM_ATENDIMENTO)
                .count();

        TimeStatusResponse status = TimeStatusResponse.builder()
                .time(time)
                .tamanhoFila(filaService.tamanhoFila(time))
                .atendimentosAtivos(ativos)
                .atendentes(atendentes)
                .fila(fila)
                .build();

        return ResponseEntity.ok(status);
    }
}
