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

import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.estrutural.ItemQuadro;
import br.com.kanban.service.CartaoService;

@DataJpaTest
@Import(CartaoService.class)
class ObservadoresCartaoTest {

    @Autowired
    private CartaoService service;

    private final HistoricoObserver historico = new HistoricoObserver();
    private final NotificacaoObserver notificacao = new NotificacaoObserver();

    @BeforeEach
    void registrar() {
        service.adicionarObservador(historico);
        service.adicionarObservador(notificacao);
    }

    @AfterEach
    void remover() {
        service.removerObservador(historico);
        service.removerObservador(notificacao);
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
}