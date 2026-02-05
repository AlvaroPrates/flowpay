package com.flowpay.atendimento.service.impl.memory;

import com.flowpay.atendimento.model.Atendimento;
import com.flowpay.atendimento.model.Time;
import com.flowpay.atendimento.service.FilaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Profile("memory")
@Slf4j
public class InMemoryFilaService implements FilaService {

    private final Map<Time, Queue<Atendimento>> filas = new ConcurrentHashMap<>();

    public InMemoryFilaService() {
        Arrays.stream(Time.values())
                .forEach(time -> filas.put(time, new ConcurrentLinkedQueue<>()));

        log.info("InMemoryFilaService inicializado com {} filas", Time.values().length);
    }

    @Override
    public void enfileirar(Atendimento atendimento) {
        if (atendimento == null) {
            log.warn("Tentativa de enfileirar atendimento null");
            return;
        }

        Time time = atendimento.getTime();
        log.info("Enfileirando atendimento ID {} no time {}",
                atendimento.getId(), time);

        filas.get(time).offer(atendimento);

        log.debug("Fila do time {} agora tem {} atendimentos",
                time, filas.get(time).size());
    }

    @Override
    public Atendimento desenfileirar(Time time) {
        Atendimento atendimento = filas.get(time).poll();

        if (atendimento != null) {
            log.info("Desenfileirado atendimento ID {} do time {}",
                    atendimento.getId(), time);
        } else {
            log.debug("Fila do time {} está vazia", time);
        }

        return atendimento;
    }

    @Override
    public List<Atendimento> listarFila(Time time) {
        return new ArrayList<>(filas.get(time));
    }

    @Override
    public int tamanhoFila(Time time) {
        return filas.get(time).size();
    }

    @Override
    public void limparFila(Time time) {
        int tamanhoAntes = filas.get(time).size();
        filas.get(time).clear();
        log.info("Fila do time {} limpa. Removidos {} atendimentos",
                time, tamanhoAntes);
    }
}
