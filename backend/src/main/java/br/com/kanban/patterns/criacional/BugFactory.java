package br.com.kanban.patterns.criacional;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.TipoCartao;

public class BugFactory extends CartaoFactory {

    @Override
    public Cartao criar(long id, String titulo, String descricao, String detalhe) {
        return new Cartao(id, TipoCartao.BUG, titulo, descricao,
                exigirDetalhe(detalhe, "passos para reprodução"));
    }
}
