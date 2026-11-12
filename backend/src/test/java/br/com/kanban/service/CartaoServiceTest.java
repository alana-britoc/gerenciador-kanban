package br.com.kanban.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;

class CartaoServiceTest {

    @Test
    void cartaoNovoComecaEmAFazer() {
        Cartao c = new CartaoService().criar(TipoCartao.TAREFA, "teste", "");
        assertEquals(Coluna.A_FAZER, c.getColuna());
    }

    @Test
    void moverTrocaAColuna() {
        CartaoService service = new CartaoService();
        Cartao c = service.criar(TipoCartao.BUG, "teste", "");
        assertEquals(Coluna.FAZENDO, service.mover(c.getId(), Coluna.FAZENDO).getColuna());
    }
}
