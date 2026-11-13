package br.com.kanban.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;

class CartaoServiceTest {

    @Test
    void cartaoNovoComecaEmAFazer() {
        Cartao c = new CartaoService().criar(TipoCartao.TAREFA, "teste", "", "Concluído");
        assertEquals(Coluna.A_FAZER, c.getColuna());
    }

    @Test
    void moverTrocaAColuna() {
        CartaoService service = new CartaoService();
        Cartao c = service.criar(TipoCartao.BUG, "teste", "", "Abrir o quadro");
        assertEquals(Coluna.FAZENDO, service.mover(c.getId(), Coluna.FAZENDO).getColuna());
    }

    @Test
    void criarDelegaAsRegrasEListarRemoverContinuamFuncionando() {
        CartaoService service = new CartaoService();
        int quantidade = service.listar().size();

        assertThrows(IllegalArgumentException.class,
                () -> service.criar(TipoCartao.MELHORIA, "Busca", "", " "));
        assertEquals(quantidade, service.listar().size());

        Cartao cartao = service.criar(TipoCartao.MELHORIA, "Busca", "", "Facilita encontrar cartões");
        assertEquals(TipoCartao.MELHORIA, cartao.getTipo());
        assertEquals("Facilita encontrar cartões", cartao.getDetalhe());
        assertEquals(quantidade + 1, service.listar().size());
        service.remover(cartao.getId());
        assertEquals(quantidade, service.listar().size());
    }
}
