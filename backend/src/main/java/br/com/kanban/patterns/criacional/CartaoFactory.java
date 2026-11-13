package br.com.kanban.patterns.criacional;

import br.com.kanban.domain.Cartao;

public abstract class CartaoFactory {

    /** Cria um cartão; cada tipo valida o significado de detalhe. */
    public abstract Cartao criar(long id, String titulo, String descricao, String detalhe);

    protected String exigirDetalhe(String detalhe, String nome) {
        if (detalhe == null || detalhe.isBlank()) {
            throw new IllegalArgumentException(nome + " é obrigatório");
        }
        return detalhe.trim();
    }
}
