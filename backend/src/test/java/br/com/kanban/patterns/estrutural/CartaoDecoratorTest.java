package br.com.kanban.patterns.estrutural;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.Extras;
import br.com.kanban.domain.Prioridade;
import br.com.kanban.domain.TipoCartao;

class CartaoDecoratorTest {

    private final Cartao cartao = new Cartao(1, TipoCartao.BUG, "Login falha", "", "Abrir a tela de login");

    @Test
    void cartaoSemDecoradorNaoTemExtras() {
        assertEquals(Extras.NENHUM, cartao.getExtras());
    }

    @Test
    void decoradoresEmpilhadosSomamOsExtras() {
        LocalDate prazo = LocalDate.of(2026, 12, 1);
        ItemQuadro item = new ComEtiqueta(new ComPrazo(new ComPrioridade(cartao, Prioridade.ALTA), prazo), "front");

        Extras extras = item.getExtras();
        assertEquals(prazo, extras.prazo());
        assertEquals(Prioridade.ALTA, extras.prioridade());
        assertEquals(List.of("front"), extras.etiquetas());
        assertEquals(Extras.NENHUM, cartao.getExtras());
    }

    @Test
    void decoradorRepassaOsDadosDoCartao() {
        ItemQuadro item = new ComPrioridade(new ComEtiqueta(cartao, "back"), Prioridade.BAIXA);

        assertEquals(1, item.getId());
        assertEquals(TipoCartao.BUG, item.getTipo());
        assertEquals("Login falha", item.getTitulo());
        assertEquals("Abrir a tela de login", item.getDetalhe());

        cartao.setColuna(Coluna.FEITO);
        assertEquals(Coluna.FEITO, item.getColuna());
    }

    @Test
    void decoradorMaisExternoDefineOValor() {
        ItemQuadro item = new ComPrioridade(new ComPrioridade(cartao, Prioridade.BAIXA), Prioridade.ALTA);
        assertEquals(Prioridade.ALTA, item.getExtras().prioridade());
    }

    @Test
    void etiquetasSeAcumulamSemRepetir() {
        ItemQuadro item = new ComEtiqueta(new ComEtiqueta(new ComEtiqueta(cartao, "front"), "urgente"), " front ");
        assertEquals(List.of("front", "urgente"), item.getExtras().etiquetas());
    }

    @Test
    void etiquetaVaziaEhRejeitada() {
        assertThrows(IllegalArgumentException.class, () -> new ComEtiqueta(cartao, " "));
    }

    @Test
    void tipoExtraConverteOValorNoDecoradorCerto() {
        ItemQuadro item = TipoExtra.PRAZO.aplicar(cartao, "2026-12-01");
        item = TipoExtra.PRIORIDADE.aplicar(item, "media");
        item = TipoExtra.ETIQUETA.aplicar(item, "docs");

        assertEquals(new Extras(LocalDate.of(2026, 12, 1), Prioridade.MEDIA, List.of("docs")), item.getExtras());
        assertThrows(IllegalArgumentException.class, () -> TipoExtra.PRAZO.aplicar(cartao, "01/12/2026"));
        assertThrows(IllegalArgumentException.class, () -> TipoExtra.PRIORIDADE.aplicar(cartao, "urgente"));
    }
}
