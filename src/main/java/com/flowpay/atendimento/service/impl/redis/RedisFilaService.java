package com.flowpay.atendimento.service.impl.redis;

import com.flowpay.atendimento.model.Atendimento;
import com.flowpay.atendimento.model.Time;
import com.flowpay.atendimento.service.FilaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação Redis do serviço de filas.
 *
 * Estrutura Redis:
 * - Key: "fila:CARTOES" → Redis List (FIFO)
 * - RPUSH adiciona no final (enfileirar)
 * - LPOP remove do início (desenfileirar)
 */
@Service
@Profile("redis")
@RequiredArgsConstructor
@Slf4j
public class RedisFilaService implements FilaService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String FILA_PREFIX = "fila:";

    private String getFilaKey(Time time) {
        return FILA_PREFIX + time.name();
    }

    @Override
    public void enfileirar(Atendimento atendimento) {
        if (atendimento == null) {
            log.warn("Tentativa de enfileirar atendimento null");
            return;
        }

        String key = getFilaKey(atendimento.getTime());
        log.info("Enfileirando no Redis: key={}, atendimentoId={}", key, atendimento.getId());

        redisTemplate.opsForList().rightPush(key, atendimento);

        log.debug("Fila Redis '{}' agora tem {} itens", key, redisTemplate.opsForList().size(key));
    }

    @Override
    public Atendimento desenfileirar(Time time) {
        String key = getFilaKey(time);
        Object obj = redisTemplate.opsForList().leftPop(key);

        if (obj != null) {
            Atendimento atendimento = (Atendimento) obj;
            log.info("Desenfileirado do Redis: key={}, atendimentoId={}", key, atendimento.getId());
            return atendimento;
        }

        log.debug("Fila Redis '{}' está vazia", key);
        return null;
    }

    @Override
    public List<Atendimento> listarFila(Time time) {
        String key = getFilaKey(time);
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);

        if (objects == null || objects.isEmpty()) {
            return new ArrayList<>();
        }

        List<Atendimento> atendimentos = new ArrayList<>();
        for (Object obj : objects) {
            atendimentos.add((Atendimento) obj);
        }

        return atendimentos;
    }

    @Override
    public int tamanhoFila(Time time) {
        String key = getFilaKey(time);
        Long size = redisTemplate.opsForList().size(key);
        return size != null ? size.intValue() : 0;
    }

    @Override
    public void limparFila(Time time) {
        String key = getFilaKey(time);
        Long tamanho = redisTemplate.opsForList().size(key);
        redisTemplate.delete(key);
        log.info("Fila Redis '{}' limpa. Removidos {} atendimentos", key, tamanho);
    }
}
