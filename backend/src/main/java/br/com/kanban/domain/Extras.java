package br.com.kanban.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record Extras(LocalDate prazo, Prioridade prioridade, List<String> etiquetas) {

    public static final Extras NENHUM = new Extras(null, null, List.of());

    public Extras {
        etiquetas = List.copyOf(etiquetas);
    }

    public Extras comPrazo(LocalDate novoPrazo) {
        return new Extras(novoPrazo, prioridade, etiquetas);
    }

    public Extras comPrioridade(Prioridade novaPrioridade) {
        return new Extras(prazo, novaPrioridade, etiquetas);
    }

    public Extras comEtiqueta(String etiqueta) {
        if (etiquetas.contains(etiqueta)) {
            return this;
        }
        List<String> novas = new ArrayList<>(etiquetas);
        novas.add(etiqueta);
        return new Extras(prazo, prioridade, novas);
    }
}
