package br.com.kanban.patterns.estrutural;

import java.util.Objects;

import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.Extras;
import br.com.kanban.domain.TipoCartao;

public abstract class CartaoDecorator implements ItemQuadro {

    protected final ItemQuadro item;

    protected CartaoDecorator(ItemQuadro item) {
        this.item = Objects.requireNonNull(item);
    }

    @Override
    public long getId() { return item.getId(); }

    @Override
    public TipoCartao getTipo() { return item.getTipo(); }

    @Override
    public String getTitulo() { return item.getTitulo(); }

    @Override
    public String getDescricao() { return item.getDescricao(); }

    @Override
    public String getDetalhe() { return item.getDetalhe(); }

    @Override
    public Coluna getColuna() { return item.getColuna(); }

    @Override
    public Extras getExtras() { return item.getExtras(); }
}
