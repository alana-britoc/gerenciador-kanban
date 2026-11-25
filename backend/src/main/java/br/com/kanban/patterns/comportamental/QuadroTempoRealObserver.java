package br.com.kanban.patterns.comportamental;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.kanban.domain.Coluna;
import br.com.kanban.websocket.QuadroWebSocketHandler;
import tools.jackson.databind.json.JsonMapper;

/**
 * Publica cada movimento no WebSocket, para todo quadro aberto se atualizar na hora.
 * O CartaoService só avisa os observadores depois do commit, então quem recarregar
 * o quadro já vê a coluna nova, e um movimento desfeito não chega a ninguém.
 */
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
        websocket.enviarATodos(json.writeValueAsString(new Mensagem(CARTAO_MOVIDO, evento.cartao().getId(),
                evento.colunaOrigem(), evento.colunaDestino(), evento.horario())));
    }
}
