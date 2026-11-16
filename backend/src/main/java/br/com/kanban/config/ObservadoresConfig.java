package br.com.kanban.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;

import br.com.kanban.patterns.comportamental.ObservadorCartao;
import br.com.kanban.service.CartaoService;

/** Registra no CartaoService todo bean que implementa ObservadorCartao. */
@Configuration
public class ObservadoresConfig {

    public ObservadoresConfig(CartaoService service, ObjectProvider<ObservadorCartao> observadores) {
        observadores.orderedStream().forEach(service::adicionarObservador);
    }
}