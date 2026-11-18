package br.com.kanban.patterns.comportamental;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import br.com.kanban.domain.Coluna;
import br.com.kanban.websocket.QuadroWebSocketHandler;
import tools.jackson.databind.json.JsonMapper;

/** Publica cada movimento no WebSocket, para todo quadro aberto se atualizar na hora. */
@Component
public class QuadroTempoRealObserver implements ObservadorCartao {

    public static final String CARTAO_MOVIDO = "CARTAO_MOVIDO";

    /** O que vai pelo WebSocket: só o necessário para o front saber o que mudou. */
    public record Mensagem(String evento, long cartaoId, Coluna origem, Coluna destino, LocalDateTime horario) {}

    private final QuadroWebSocketHandler websocket;
    private final JsonMapper json;

    public QuadroTempoRealObserver(QuadroWebSocketHandler websocket, JsonMapper json) {
        this.websocket = websocket;
        this.json = json;
    }

    @Override
    public void aoMoverCartao(MovimentoCartaoEvento evento) {
        String texto = json.writeValueAsString(new Mensagem(CARTAO_MOVIDO, evento.cartao().getId(),
                evento.colunaOrigem(), evento.colunaDestino(), evento.horario()));
        depoisDoCommit(() -> websocket.enviarATodos(texto));
    }

    // O aviso só sai depois que o movimento foi gravado: se a transação for
    // desfeita ninguém é avisado, e quem recarregar o quadro já vê a coluna nova.
    private static void depoisDoCommit(Runnable acao) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            acao.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                acao.run();
            }
        });
    }
}
