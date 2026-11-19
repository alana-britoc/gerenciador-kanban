package br.com.kanban.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.kanban.patterns.comportamental.HistoricoObserver;
import br.com.kanban.patterns.comportamental.NotificacaoObserver;

@RestController
@RequestMapping("/api")
public class AtividadeController {

    private final HistoricoObserver historico;
    private final NotificacaoObserver notificacoes;

    public AtividadeController(HistoricoObserver historico, NotificacaoObserver notificacoes) {
        this.historico = historico;
        this.notificacoes = notificacoes;
    }

    @GetMapping("/historico")
    public List<HistoricoObserver.Registro> historico() {
        return historico.registros();
    }

    @GetMapping("/notificacoes")
    public List<NotificacaoObserver.Notificacao> notificacoes() {
        return notificacoes.notificacoes();
    }
}