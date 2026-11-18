package br.com.kanban.websocket;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import br.com.kanban.config.WebSocketConfig;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.comportamental.QuadroTempoRealObserver;
import br.com.kanban.service.CartaoService;
import tools.jackson.databind.json.JsonMapper;

// sobe o servidor de verdade; banco separado porque este teste grava (não usa rollback)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:kanban-websocket;DB_CLOSE_DELAY=-1")
class QuadroTempoRealTest {

    @LocalServerPort
    private int porta;

    @Autowired
    private CartaoService service;

    @Autowired
    private QuadroWebSocketHandler handler;

    @Autowired
    private TransactionTemplate transacao;

    @Autowired
    private JsonMapper json;

    private final List<WebSocketSession> abas = new ArrayList<>();

    /** Uma "aba" com o quadro aberto: guarda tudo o que recebe pelo WebSocket. */
    private BlockingQueue<String> abrirAba() throws Exception {
        BlockingQueue<String> recebidas = new LinkedBlockingQueue<>();
        WebSocketSession sessao = new StandardWebSocketClient()
                .execute(new TextWebSocketHandler() {
                    @Override
                    protected void handleTextMessage(WebSocketSession s, TextMessage mensagem) {
                        recebidas.add(mensagem.getPayload());
                    }
                }, "ws://localhost:" + porta + WebSocketConfig.ROTA)
                .get(5, TimeUnit.SECONDS);
        abas.add(sessao);
        return recebidas;
    }

    /** O cliente pode terminar o handshake antes de o servidor registrar a sessão. */
    private void esperarConexoes(int quantidade) throws InterruptedException {
        long limite = System.currentTimeMillis() + 5_000;
        while (handler.conectados() < quantidade && System.currentTimeMillis() < limite) {
            Thread.sleep(20);
        }
        assertEquals(quantidade, handler.conectados());
    }

    @AfterEach
    void fecharAbas() throws Exception {
        for (WebSocketSession aba : abas) {
            aba.close();
        }
    }

    @Test
    void moverAvisaTodasAsAbasAbertas() throws Exception {
        BlockingQueue<String> aba1 = abrirAba();
        BlockingQueue<String> aba2 = abrirAba();
        esperarConexoes(2);
        long id = service.criar(TipoCartao.TAREFA, "Tempo real", "", "Outra aba atualiza").getId();

        service.mover(id, Coluna.FAZENDO);

        for (BlockingQueue<String> aba : List.of(aba1, aba2)) {
            String recebida = aba.poll(5, TimeUnit.SECONDS);
            assertNotNull(recebida, "a aba não recebeu o movimento");
            QuadroTempoRealObserver.Mensagem mensagem =
                    json.readValue(recebida, QuadroTempoRealObserver.Mensagem.class);
            assertEquals(QuadroTempoRealObserver.CARTAO_MOVIDO, mensagem.evento());
            assertEquals(id, mensagem.cartaoId());
            assertEquals(Coluna.A_FAZER, mensagem.origem());
            assertEquals(Coluna.FAZENDO, mensagem.destino());
            assertNotNull(mensagem.horario());
        }
    }

    @Test
    void movimentoDesfeitoNaoEAvisado() throws Exception {
        BlockingQueue<String> aba = abrirAba();
        esperarConexoes(1);
        long id = service.criar(TipoCartao.TAREFA, "Rollback", "", "Ninguém é avisado").getId();

        transacao.executeWithoutResult(status -> {
            service.mover(id, Coluna.FEITO);
            status.setRollbackOnly();
        });

        assertNull(aba.poll(500, TimeUnit.MILLISECONDS), "avisou um movimento que não foi gravado");
        assertEquals(Coluna.A_FAZER, service.item(id).getColuna());
    }
}
