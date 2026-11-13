package br.com.kanban.patterns.criacional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;

class CartaoFactoryTest {

    @Test
    void tarefaExigeCriteriosDeAceite() {
        CartaoFactory factory = new TarefaFactory();
        Cartao cartao = factory.criar(1, "Configurar", "Descrição", "  API responde  ");
        assertEquals(TipoCartao.TAREFA, cartao.getTipo());
        assertEquals("API responde", cartao.getDetalhe());
        assertEquals("Descrição", cartao.getDescricao());
        assertEquals(Coluna.A_FAZER, cartao.getColuna());
        assertThrows(IllegalArgumentException.class, () -> factory.criar(2, "Configurar", "", " "));
    }

    @Test
    void bugExigePassosParaReproducao() {
        CartaoFactory factory = new BugFactory();
        Cartao cartao = factory.criar(1, "Erro", "Descrição", "  Abrir o quadro  ");
        assertEquals(TipoCartao.BUG, cartao.getTipo());
        assertEquals("Abrir o quadro", cartao.getDetalhe());
        assertThrows(IllegalArgumentException.class, () -> factory.criar(2, "Erro", "", null));
    }

    @Test
    void melhoriaExigeJustificativa() {
        CartaoFactory factory = new MelhoriaFactory();
        Cartao cartao = factory.criar(1, "Busca", "Descrição", "  Encontrar cartões  ");
        assertEquals(TipoCartao.MELHORIA, cartao.getTipo());
        assertEquals("Encontrar cartões", cartao.getDetalhe());
        assertThrows(IllegalArgumentException.class, () -> factory.criar(2, "Busca", "", ""));
    }
}
