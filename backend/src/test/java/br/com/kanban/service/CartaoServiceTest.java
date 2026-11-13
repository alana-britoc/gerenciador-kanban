package br.com.kanban.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.Prioridade;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.estrutural.ItemQuadro;
import br.com.kanban.patterns.estrutural.TipoExtra;

class CartaoServiceTest {

    @Test
    void cartaoNovoComecaEmAFazer() {
        ItemQuadro c = new CartaoService().criar(TipoCartao.TAREFA, "teste", "", "Concluído");
        assertEquals(Coluna.A_FAZER, c.getColuna());
    }

    @Test
    void moverTrocaAColuna() {
        CartaoService service = new CartaoService();
        ItemQuadro c = service.criar(TipoCartao.BUG, "teste", "", "Abrir o quadro");
        assertEquals(Coluna.FAZENDO, service.mover(c.getId(), Coluna.FAZENDO).getColuna());
    }

    @Test
    void criarDelegaAsRegrasEListarRemoverContinuamFuncionando() {
        CartaoService service = new CartaoService();
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
        CartaoService service = new CartaoService();
        ItemQuadro c = service.criar(TipoCartao.TAREFA, "Tela", "", "Quadro aparece");

        service.adicionarExtra(c.getId(), TipoExtra.PRIORIDADE, "ALTA");
        service.adicionarExtra(c.getId(), TipoExtra.ETIQUETA, "front");
        service.mover(c.getId(), Coluna.FAZENDO);

        ItemQuadro item = service.item(c.getId());
        assertEquals(Coluna.FAZENDO, item.getColuna());
        assertEquals(Prioridade.ALTA, item.getExtras().prioridade());
        assertEquals(List.of("front"), item.getExtras().etiquetas());
        assertTrue(service.listar().contains(item));
    }

    @Test
    void extraEmCartaoInexistenteFalha() {
        assertThrows(NoSuchElementException.class,
                () -> new CartaoService().adicionarExtra(999, TipoExtra.ETIQUETA, "front"));
    }

    @Test
    void bugNasceComPrioridadeAltaEPermiteSubstituir() {
        CartaoService service = new CartaoService();
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
}
