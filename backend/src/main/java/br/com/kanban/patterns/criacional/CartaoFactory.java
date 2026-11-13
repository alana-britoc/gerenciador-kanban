package br.com.kanban.patterns.criacional;

import br.com.kanban.domain.Cartao;
import br.com.kanban.patterns.estrutural.ItemQuadro;

public abstract class CartaoFactory {

    /** Cria um cartão; cada tipo valida o significado de detalhe. */
    public abstract Cartao criar(long id, String titulo, String descricao, String detalhe);

    /** Permite à fábrica definir extras presentes desde a criação. */
    public ItemQuadro decorarInicialmente(Cartao cartao) {
        return cartao;
    }

    protected String exigirDetalhe(String detalhe, String nome) {
        if (detalhe == null || detalhe.isBlank()) {
            throw new IllegalArgumentException(nome + " é obrigatório");
        }
        return detalhe.trim();
    }
}
