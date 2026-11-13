package br.com.kanban.patterns.estrutural;

import br.com.kanban.domain.Extras;

public class ComEtiqueta extends CartaoDecorator {

    private final String etiqueta;

    public ComEtiqueta(ItemQuadro item, String etiqueta) {
        super(item);
        if (etiqueta == null || etiqueta.isBlank()) {
            throw new IllegalArgumentException("etiqueta é obrigatória");
        }
        this.etiqueta = etiqueta.trim();
    }

    @Override
    public Extras getExtras() {
        return item.getExtras().comEtiqueta(etiqueta);
    }
}
