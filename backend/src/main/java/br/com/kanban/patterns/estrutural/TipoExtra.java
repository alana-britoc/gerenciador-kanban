package br.com.kanban.patterns.estrutural;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import br.com.kanban.domain.Prioridade;

public enum TipoExtra {

    PRAZO {
        @Override
        public ItemQuadro aplicar(ItemQuadro item, String valor) {
            try {
                return new ComPrazo(item, LocalDate.parse(obrigatorio(valor)));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("prazo deve estar no formato AAAA-MM-DD");
            }
        }
    },
    PRIORIDADE {
        @Override
        public ItemQuadro aplicar(ItemQuadro item, String valor) {
            try {
                return new ComPrioridade(item, Prioridade.valueOf(obrigatorio(valor).toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("prioridade deve ser BAIXA, MEDIA ou ALTA");
            }
        }
    },
    ETIQUETA {
        @Override
        public ItemQuadro aplicar(ItemQuadro item, String valor) {
            return new ComEtiqueta(item, valor);
        }
    };

    public abstract ItemQuadro aplicar(ItemQuadro item, String valor);

    private static String obrigatorio(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("valor do extra é obrigatório");
        }
        return valor.trim();
    }
}
