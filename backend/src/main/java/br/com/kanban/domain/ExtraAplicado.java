package br.com.kanban.domain;

import br.com.kanban.patterns.estrutural.TipoExtra;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/** Um extra adicionado a um cartão, salvo para remontar o decorador correspondente. */
@Embeddable
public record ExtraAplicado(
        @Enumerated(EnumType.STRING) @Column(nullable = false) TipoExtra tipo,
        @Column(nullable = false) String valor) {
}
