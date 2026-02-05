package com.flowpay.atendimento.service.impl.redis;

import com.flowpay.atendimento.model.Atendimento;
import com.flowpay.atendimento.model.StatusAtendimento;
import com.flowpay.atendimento.model.Time;
import com.flowpay.atendimento.service.AtendimentoService;
import com.flowpay.atendimento.service.DistribuidorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("redis")
@RequiredArgsConstructor
@Slf4j
public class RedisAtendimentoService implements AtendimentoService {

    private final DistribuidorService distribuidorService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ATENDIMENTO_PREFIX = "atendimento:";
    private static final String ATENDIMENTOS_IDS_KEY = "atendimentos:ids";
    private static final String ID_COUNTER_KEY = "atendimento:id:counter";

    private String getAtendimentoKey(Long id) {
        return ATENDIMENTO_PREFIX + id;
    }

    private Long gerarProximoId() {
        return redisTemplate.opsForValue().increment(ID_COUNTER_KEY);
    }

    @Override
    public Atendimento criar(Atendimento atendimento) {
        if (atendimento == null) {
            throw new IllegalArgumentException("Atendimento não pode ser null");
        }

        atendimento.setId(gerarProximoId());
        atendimento.setStatus(StatusAtendimento.AGUARDANDO_ATENDIMENTO);
        atendimento.setDataHoraCriacao(LocalDateTime.now());

        String key = getAtendimentoKey(atendimento.getId());
        redisTemplate.opsForValue().set(key, atendimento);
        redisTemplate.opsForSet().add(ATENDIMENTOS_IDS_KEY, atendimento.getId());

        log.info("Atendimento criado no Redis: ID={}, Cliente={}, Time={}",
                atendimento.getId(), atendimento.getNomeCliente(), atendimento.getTime());

        distribuidorService.distribuir(atendimento);

        return atendimento;
    }

    @Override
    public Optional<Atendimento> buscarPorId(Long id) {
        String key = getAtendimentoKey(id);
        Object obj = redisTemplate.opsForValue().get(key);

        if (obj == null) {
            return Optional.empty();
        }

        return Optional.of((Atendimento) obj);
    }

    @Override
    public List<Atendimento> listarPorTime(Time time) {
        return listarTodos().stream()
                .filter(a -> a.getTime() == time)
                .collect(Collectors.toList());
    }

    @Override
    public List<Atendimento> listarPorStatus(StatusAtendimento status) {
        return listarTodos().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Atendimento> listarTodos() {
        Set<Object> ids = redisTemplate.opsForSet().members(ATENDIMENTOS_IDS_KEY);

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Atendimento> atendimentos = new ArrayList<>();
        for (Object idObj : ids) {
            Long id = ((Number) idObj).longValue();
            buscarPorId(id).ifPresent(atendimentos::add);
        }

        return atendimentos;
    }
}
