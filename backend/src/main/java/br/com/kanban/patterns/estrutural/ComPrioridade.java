package br.com.kanban.patterns.estrutural;

import java.util.Objects;

import br.com.kanban.domain.Extras;
import br.com.kanban.domain.Prioridade;

public class ComPrioridade extends CartaoDecorator {

    private final Prioridade prioridade;

    public ComPrioridade(ItemQuadro item, Prioridade prioridade) {
        super(item);
        this.prioridade = Objects.requireNonNull(prioridade, "prioridade é obrigatória");
    }

    @Override
    public Extras getExtras() {
        return item.getExtras().comPrioridade(prioridade);
    }
}
