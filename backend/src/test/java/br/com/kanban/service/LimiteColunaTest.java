package br.com.kanban.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.estrutural.ItemQuadro;

@DataJpaTest
@Import(CartaoService.class)
class LimiteColunaTest {

    @Autowired
    private CartaoService service;

    private ItemQuadro novo(String titulo) {
        return service.criar(TipoCartao.TAREFA, titulo, "", "detalhe");
    }

    @Test
    void colunaCheiaRecusaOMovimentoEMantemOCartaoOndeEstava() {
        service.definirLimite(Coluna.FAZENDO, 2);
        service.mover(novo("a").getId(), Coluna.FAZENDO);
        service.mover(novo("b").getId(), Coluna.FAZENDO);
        ItemQuadro c = novo("c");

        ColunaCheiaException e = assertThrows(ColunaCheiaException.class,
                () -> service.mover(c.getId(), Coluna.FAZENDO));

        assertTrue(e.getMessage().contains("FAZENDO"));
        assertTrue(e.getMessage().contains("2"));
        assertEquals(Coluna.A_FAZER, service.item(c.getId()).getColuna());
    }

    @Test
    void semLimiteAColunaAceitaQuantosCartoesForem() {
        for (int i = 0; i < 5; i++) {
            service.mover(novo("c" + i).getId(), Coluna.FEITO);
        }

        assertEquals(5, service.colunas().get(Coluna.FEITO.ordinal()).quantidade());
    }

    @Test
    void liberarUmaVagaPermiteMoverDeNovo() {
        service.definirLimite(Coluna.FAZENDO, 1);
        ItemQuadro a = novo("a");
        ItemQuadro b = novo("b");
        service.mover(a.getId(), Coluna.FAZENDO);
        assertThrows(ColunaCheiaException.class, () -> service.mover(b.getId(), Coluna.FAZENDO));

        service.mover(a.getId(), Coluna.FEITO);

        assertEquals(Coluna.FAZENDO, service.mover(b.getId(), Coluna.FAZENDO).getColuna());
    }

    @Test
    void moverParaAColunaEmQueJaEstaNaoContaComoEntrada() {
        service.definirLimite(Coluna.FAZENDO, 1);
        ItemQuadro a = novo("a");
        service.mover(a.getId(), Coluna.FAZENDO);

        assertDoesNotThrow(() -> service.mover(a.getId(), Coluna.FAZENDO));
    }

    @Test
    void resumoMostraQuantidadeELimiteDeCadaColuna() {
        service.definirLimite(Coluna.FAZENDO, 3);
        service.mover(novo("a").getId(), Coluna.FAZENDO);
        service.mover(novo("b").getId(), Coluna.FAZENDO);

        CartaoService.ResumoColuna fazendo = service.colunas().get(Coluna.FAZENDO.ordinal());
        assertEquals(2, fazendo.quantidade());
        assertEquals(3, fazendo.limite());
        assertNull(service.colunas().get(Coluna.A_FAZER.ordinal()).limite());
    }
}