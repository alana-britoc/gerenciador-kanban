package br.com.kanban.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.Extras;
import br.com.kanban.domain.Prioridade;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.estrutural.ItemQuadro;
import br.com.kanban.patterns.estrutural.TipoExtra;

@DataJpaTest
@Import(CartaoService.class)
class CartaoServiceTest {

    @Autowired
    private CartaoService service;

    @Autowired
    private TestEntityManager em;

    /** Grava no banco e descarta o cache do JPA, para a próxima leitura vir do banco. */
    private void recarregarDoBanco() {
        em.flush();
        em.clear();
    }

    @Test
    void cartaoNovoComecaEmAFazer() {
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "teste", "", "Concluído");
        assertEquals(Coluna.A_FAZER, c.getColuna());
    }

    @Test
    void moverTrocaAColuna() {
        ItemQuadro c = service.criar(TipoCartao.BUG, "teste", "", "Abrir o quadro");
        assertEquals(Coluna.FAZENDO, service.mover(c.getId(), Coluna.FAZENDO).getColuna());
    }

    @Test
    void criarDelegaAsRegrasEListarRemoverContinuamFuncionando() {
        int quantidade = service.listar().size();

        assertThrows(IllegalArgumentException.class,
                () -> service.criar(TipoCartao.MELHORIA, "Busca", "", " "));
        assertEquals(quantidade, service.listar().size());

        ItemQuadro cartao = service.criar(TipoCartao.MELHORIA, "Busca", "", "Facilita encontrar cartões");
        assertEquals(TipoCartao.MELHORIA, cartao.getTipo());
        assertEquals("Facilita encontrar cartões", cartao.getDetalhe());
        assertEquals(quantidade + 1, service.listar().size());
        service.remover(cartao.getId());
        assertEquals(quantidade, service.listar().size());
    }

    @Test
    void extrasSeMantemAoMoverEAparecemNaListagem() {
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "Tela", "", "Quadro aparece");

        service.adicionarExtra(c.getId(), TipoExtra.PRIORIDADE, "ALTA");
        service.adicionarExtra(c.getId(), TipoExtra.ETIQUETA, "front");
        service.mover(c.getId(), Coluna.FAZENDO);

        ItemQuadro item = service.item(c.getId());
        assertEquals(Coluna.FAZENDO, item.getColuna());
        assertEquals(Prioridade.ALTA, item.getExtras().prioridade());
        assertEquals(List.of("front"), item.getExtras().etiquetas());

        ItemQuadro listado = service.listar().stream()
                .filter(i -> i.getId() == c.getId()).findFirst().orElseThrow();
        assertEquals(item.getExtras(), listado.getExtras());
    }

    @Test
    void extraEmCartaoInexistenteFalha() {
        assertThrows(NoSuchElementException.class,
                () -> service.adicionarExtra(999, TipoExtra.ETIQUETA, "front"));
    }

    @Test
    void bugNasceComPrioridadeAltaEPermiteSubstituir() {
        ItemQuadro bug = service.criar(TipoCartao.BUG, "Falha", "", "Abrir o quadro");

        assertEquals(Prioridade.ALTA, bug.getExtras().prioridade());
        assertEquals(Prioridade.ALTA, service.item(bug.getId()).getExtras().prioridade());
        assertEquals(Prioridade.ALTA, service.listar().stream()
                .filter(item -> item.getId() == bug.getId()).findFirst().orElseThrow().getExtras().prioridade());

        service.adicionarExtra(bug.getId(), TipoExtra.PRIORIDADE, "BAIXA");
        assertEquals(Prioridade.BAIXA, service.item(bug.getId()).getExtras().prioridade());
        service.mover(bug.getId(), Coluna.FAZENDO);
        assertEquals(Coluna.FAZENDO, service.item(bug.getId()).getColuna());
    }

    @Test
    void cartaoEExtrasSaoLidosDoBanco() {
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "Persistir", "Descrição", "Dados voltam");
        service.adicionarExtra(c.getId(), TipoExtra.PRAZO, "2026-12-01");
        service.adicionarExtra(c.getId(), TipoExtra.PRIORIDADE, "BAIXA");
        service.adicionarExtra(c.getId(), TipoExtra.ETIQUETA, "back");
        service.adicionarExtra(c.getId(), TipoExtra.PRIORIDADE, "MEDIA");
        service.mover(c.getId(), Coluna.FEITO);
        recarregarDoBanco();

        ItemQuadro lido = service.item(c.getId());
        assertEquals("Persistir", lido.getTitulo());
        assertEquals("Dados voltam", lido.getDetalhe());
        assertEquals(Coluna.FEITO, lido.getColuna());
        // a ordem salva é respeitada: a prioridade aplicada por último vale
        assertEquals(new Extras(LocalDate.of(2026, 12, 1), Prioridade.MEDIA, List.of("back")), lido.getExtras());
    }

    @Test
    void bugLidoDoBancoMantemPrioridadeInicial() {
        ItemQuadro bug = service.criar(TipoCartao.BUG, "Falha", "", "Abrir o quadro");
        recarregarDoBanco();

        assertEquals(Prioridade.ALTA, service.item(bug.getId()).getExtras().prioridade());
    }

    @Test
    void extraInvalidoNaoESalvo() {
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "Tela", "", "Quadro aparece");

        assertThrows(IllegalArgumentException.class,
                () -> service.adicionarExtra(c.getId(), TipoExtra.PRAZO, "amanha"));
        recarregarDoBanco();

        assertEquals(Extras.NENHUM, service.item(c.getId()).getExtras());
    }
}
