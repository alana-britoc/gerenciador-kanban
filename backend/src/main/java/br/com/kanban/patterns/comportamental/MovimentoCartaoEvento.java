package br.com.kanban.patterns.comportamental;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;

import java.time.LocalDateTime;

public record MovimentoCartaoEvento(
        Cartao cartao,
        Coluna colunaOrigem,
        Coluna colunaDestino,
        LocalDateTime horario) {
}