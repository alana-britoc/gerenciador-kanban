package br.com.kanban.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Mantém as conexões de quem está com o quadro aberto e envia mensagens a todas.
 * O front só escuta; mensagens vindas dele são ignoradas.
 */
@Component
public class QuadroWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(QuadroWebSocketHandler.class);
    private static final int TEMPO_LIMITE_ENVIO_MS = 5_000;
    private static final int LIMITE_BUFFER_BYTES = 64 * 1024;

    private final Map<String, WebSocketSession> sessoes = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession sessao) {
        // o decorador permite enviar para a mesma sessão a partir de várias threads
        sessoes.put(sessao.getId(),
                new ConcurrentWebSocketSessionDecorator(sessao, TEMPO_LIMITE_ENVIO_MS, LIMITE_BUFFER_BYTES));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession sessao, CloseStatus status) {
        sessoes.remove(sessao.getId());
    }

    public void enviarATodos(String texto) {
        TextMessage mensagem = new TextMessage(texto);
        for (WebSocketSession sessao : sessoes.values()) {
            try {
                if (sessao.isOpen()) {
                    sessao.sendMessage(mensagem);
                }
            } catch (IOException | RuntimeException e) {
                // uma aba com problema não pode impedir as outras de receber
                log.warn("não foi possível enviar para a sessão {}: {}", sessao.getId(), e.getMessage());
                sessoes.remove(sessao.getId());
            }
        }
    }

    public int conectados() {
        return sessoes.size();
    }
}
