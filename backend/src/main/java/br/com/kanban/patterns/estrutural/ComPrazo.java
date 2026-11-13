package br.com.kanban.patterns.estrutural;

import java.time.LocalDate;
import java.util.Objects;

import br.com.kanban.domain.Extras;

public class ComPrazo extends CartaoDecorator {

    private final LocalDate prazo;

    public ComPrazo(ItemQuadro item, LocalDate prazo) {
        super(item);
        this.prazo = Objects.requireNonNull(prazo, "prazo é obrigatório");
    }

    @Override
    public Extras getExtras() {
        return item.getExtras().comPrazo(prazo);
    }
}
