package com.flowpay.atendimento.controller;

import com.flowpay.atendimento.dto.request.CriarAtendimentoRequest;
import com.flowpay.atendimento.dto.response.AtendimentoResponse;
import com.flowpay.atendimento.exception.ErrorResponse;
import com.flowpay.atendimento.exception.RecursoNaoEncontradoException;
import com.flowpay.atendimento.model.Atendimento;
import com.flowpay.atendimento.model.StatusAtendimento;
import com.flowpay.atendimento.model.Time;
import com.flowpay.atendimento.service.AtendimentoService;
import com.flowpay.atendimento.service.DistribuidorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Atendimentos", description = "Gerenciamento de atendimentos e distribuição")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;
    private final DistribuidorService distribuidorService;

    @Operation(
        summary = "Criar novo atendimento",
        description = "Cria um novo atendimento e o distribui automaticamente para um atendente disponível. " +
                     "Se não houver atendentes disponíveis, o atendimento é enfileirado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Atendimento criado com sucesso",
            content = @Content(schema = @Schema(implementation = AtendimentoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos na requisição",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<AtendimentoResponse> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do atendimento a ser criado",
                required = true
            )
            @Valid @RequestBody CriarAtendimentoRequest request) {

        log.info("Recebida requisição para criar atendimento: cliente={}, time={}",
                request.getNomeCliente(), request.getTime());

        Atendimento atendimento = Atendimento.builder()
                .nomeCliente(request.getNomeCliente())
                .assunto(request.getAssunto())
                .time(request.getTime())
                .build();

        Atendimento criado = atendimentoService.criar(atendimento);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AtendimentoResponse.fromEntity(criado));
    }

    @Operation(
        summary = "Listar todos os atendimentos",
        description = "Retorna uma lista com todos os atendimentos cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de atendimentos retornada com sucesso"
        )
    })
    @GetMapping
    public ResponseEntity<List<AtendimentoResponse>> listarTodos() {
        List<AtendimentoResponse> atendimentos = atendimentoService.listarTodos()
                .stream()
                .map(AtendimentoResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(atendimentos);
    }

    @Operation(
        summary = "Buscar atendimento por ID",
        description = "Retorna os detalhes de um atendimento específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Atendimento encontrado",
            content = @Content(schema = @Schema(implementation = AtendimentoResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Atendimento não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoResponse> buscarPorId(
            @Parameter(description = "ID do atendimento", example = "1")
            @PathVariable Long id) {
        Atendimento atendimento = atendimentoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Atendimento não encontrado: " + id));

        return ResponseEntity.ok(AtendimentoResponse.fromEntity(atendimento));
    }

    @Operation(
        summary = "Listar atendimentos por time",
        description = "Retorna todos os atendimentos de um time específico"
    )
    @GetMapping("/time/{time}")
    public ResponseEntity<List<AtendimentoResponse>> listarPorTime(
            @Parameter(description = "Time do atendimento", example = "CARTOES")
            @PathVariable Time time) {
        List<AtendimentoResponse> atendimentos = atendimentoService.listarPorTime(time)
                .stream()
                .map(AtendimentoResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(atendimentos);
    }

    @Operation(
        summary = "Listar atendimentos por status",
        description = "Retorna todos os atendimentos com um status específico"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AtendimentoResponse>> listarPorStatus(
            @Parameter(description = "Status do atendimento", example = "EM_ATENDIMENTO")
            @PathVariable StatusAtendimento status) {

        List<AtendimentoResponse> atendimentos = atendimentoService.listarPorStatus(status)
                .stream()
                .map(AtendimentoResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(atendimentos);
    }

    @Operation(
        summary = "Finalizar atendimento",
        description = "Marca um atendimento como finalizado e libera o atendente. " +
                     "Automaticamente processa a fila para distribuir o próximo atendimento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Atendimento finalizado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Atendimento não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizar(
            @Parameter(description = "ID do atendimento a ser finalizado", example = "1")
            @PathVariable Long id) {
        log.info("Recebida requisição para finalizar atendimento: id={}", id);

        atendimentoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Atendimento não encontrado: " + id));

        distribuidorService.finalizarAtendimento(id);

        return ResponseEntity.noContent().build();
    }
}
