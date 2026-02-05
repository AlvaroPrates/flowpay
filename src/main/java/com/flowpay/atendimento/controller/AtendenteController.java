package com.flowpay.atendimento.controller;

import com.flowpay.atendimento.dto.request.CadastrarAtendenteRequest;
import com.flowpay.atendimento.dto.response.AtendenteResponse;
import com.flowpay.atendimento.exception.ErrorResponse;
import com.flowpay.atendimento.exception.RecursoNaoEncontradoException;
import com.flowpay.atendimento.model.Atendente;
import com.flowpay.atendimento.model.Time;
import com.flowpay.atendimento.service.AtendenteService;
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
@RequestMapping("/api/atendentes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Atendentes", description = "Gerenciamento de atendentes")
public class AtendenteController {

    private final AtendenteService atendenteService;

    @Operation(
        summary = "Cadastrar novo atendente",
        description = "Cadastra um novo atendente no sistema e o associa a um time específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Atendente cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = AtendenteResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<AtendenteResponse> cadastrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do atendente",
                required = true
            )
            @Valid @RequestBody CadastrarAtendenteRequest request) {

        log.info("Recebida requisição para cadastrar atendente: nome={}, time={}",
                request.getNome(), request.getTime());

        Atendente atendente = Atendente.builder()
                .nome(request.getNome())
                .time(request.getTime())
                .build();

        Atendente cadastrado = atendenteService.cadastrar(atendente);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AtendenteResponse.fromEntity(cadastrado));
    }

    @Operation(
        summary = "Listar todos os atendentes",
        description = "Retorna uma lista com todos os atendentes cadastrados"
    )
    @GetMapping
    public ResponseEntity<List<AtendenteResponse>> listarTodos() {
        List<AtendenteResponse> atendentes = atendenteService.listarTodos()
                .stream()
                .map(AtendenteResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(atendentes);
    }

    @Operation(
        summary = "Buscar atendente por ID",
        description = "Retorna os detalhes de um atendente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Atendente encontrado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Atendente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AtendenteResponse> buscarPorId(
            @Parameter(description = "ID do atendente", example = "1")
            @PathVariable Long id) {
        Atendente atendente = atendenteService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Atendente não encontrado: " + id));

        return ResponseEntity.ok(AtendenteResponse.fromEntity(atendente));
    }

    @Operation(
        summary = "Listar atendentes por time",
        description = "Retorna todos os atendentes de um time específico"
    )
    @GetMapping("/time/{time}")
    public ResponseEntity<List<AtendenteResponse>> listarPorTime(
            @Parameter(description = "Time", example = "CARTOES")
            @PathVariable Time time) {
        List<AtendenteResponse> atendentes = atendenteService.listarPorTime(time)
                .stream()
                .map(AtendenteResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(atendentes);
    }

    @Operation(
        summary = "Listar atendentes disponíveis",
        description = "Retorna apenas os atendentes disponíveis (com menos de 3 atendimentos ativos) de um time"
    )
    @GetMapping("/time/{time}/disponiveis")
    public ResponseEntity<List<AtendenteResponse>> listarDisponiveis(
            @Parameter(description = "Time", example = "CARTOES")
            @PathVariable Time time) {
        List<AtendenteResponse> disponiveis = atendenteService.buscarDisponiveisPorTime(time)
                .stream()
                .map(AtendenteResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(disponiveis);
    }
}
