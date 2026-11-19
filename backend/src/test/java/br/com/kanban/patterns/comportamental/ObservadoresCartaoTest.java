package br.com.kanban.patterns.comportamental;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.estrutural.ItemQuadro;
import br.com.kanban.repository.CartaoRepository;
import br.com.kanban.service.CartaoService;

// sem transação no teste: os eventos só saem depois do commit do serviço
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import(CartaoService.class)
class ObservadoresCartaoTest {

    @Autowired
    private CartaoService service;

    @Autowired
    private CartaoRepository repositorio;

    @Autowired
    private PlatformTransactionManager transacoes;

    private final HistoricoObserver historico = new HistoricoObserver();
    private final NotificacaoObserver notificacao = new NotificacaoObserver();

    @BeforeEach
    void registrar() {
        service.adicionarObservador(historico);
        service.adicionarObservador(notificacao);
    }

    // como os testes gravam de verdade, apaga os cartões para não vazar para outros testes
    @AfterEach
    void remover() {
        service.removerObservador(historico);
        service.removerObservador(notificacao);
        repositorio.deleteAll();
    }

    @Test
    void osDoisObservadoresRecebemOEventoAoChegarEmFeito() {
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "Tela", "", "Quadro aparece");

        service.mover(c.getId(), Coluna.FEITO);

        assertEquals(1, historico.registros().size());
        HistoricoObserver.Registro r = historico.registros().get(0);
        assertEquals(c.getId(), r.cartaoId());
        assertEquals(Coluna.A_FAZER, r.origem());
        assertEquals(Coluna.FEITO, r.destino());
        assertNotNull(r.horario());

        assertEquals(1, notificacao.notificacoes().size());
        assertEquals(c.getId(), notificacao.notificacoes().get(0).cartaoId());
    }

    @Test
    void notificacaoSoQuandoOCartaoChegaEmFeito() {
        ItemQuadro c = service.criar(TipoCartao.BUG, "Falha", "", "Abrir o quadro");

        service.mover(c.getId(), Coluna.FAZENDO);

        assertEquals(1, historico.registros().size());
        assertTrue(notificacao.notificacoes().isEmpty());
    }

    @Test
    void moverParaAMesmaColunaNaoGeraEvento() {
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "Tela", "", "Quadro aparece");

        service.mover(c.getId(), Coluna.A_FAZER);

        assertTrue(historico.registros().isEmpty());
    }

    @Test
    void observadorRemovidoNaoRecebeMaisEventos() {
        List<MovimentoCartaoEvento> eventos = new ArrayList<>();
        ObservadorCartao qualquer = eventos::add;
        service.adicionarObservador(qualquer);
        ItemQuadro c = service.criar(TipoCartao.MELHORIA, "Busca", "", "Facilita encontrar");

        service.mover(c.getId(), Coluna.FAZENDO);
        service.removerObservador(qualquer);
        service.mover(c.getId(), Coluna.FEITO);

        assertEquals(1, eventos.size());
        assertEquals(2, historico.registros().size());
    }

    @Test
    void rollbackNaoGeraEvento() {
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "Tela", "", "Quadro aparece");

        assertThrows(IllegalStateException.class,
                () -> new TransactionTemplate(transacoes).executeWithoutResult(status -> {
                    service.mover(c.getId(), Coluna.FEITO);
                    throw new IllegalStateException("falha depois de mover");
                }));

        assertTrue(historico.registros().isEmpty());
        assertTrue(notificacao.notificacoes().isEmpty());
    }

    @Test
    void falhaDeUmObservadorNaoImpedeOsOutros() {
        ObservadorCartao quebrado = evento -> {
            throw new IllegalStateException("observador com defeito");
        };
        service.adicionarObservador(quebrado);
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "Tela", "", "Quadro aparece");

        try {
            assertDoesNotThrow(() -> service.mover(c.getId(), Coluna.FEITO));
        } finally {
            service.removerObservador(quebrado);
        }

        assertEquals(1, historico.registros().size());
        assertEquals(1, notificacao.notificacoes().size());
    }
}