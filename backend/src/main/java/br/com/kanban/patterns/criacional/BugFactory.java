package br.com.kanban.patterns.criacional;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Prioridade;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.estrutural.ComPrioridade;
import br.com.kanban.patterns.estrutural.ItemQuadro;

public class BugFactory extends CartaoFactory {

    @Override
    public Cartao criar(long id, String titulo, String descricao, String detalhe) {
        return new Cartao(id, TipoCartao.BUG, titulo, descricao,
                exigirDetalhe(detalhe, "passos para reprodução"));
    }

    @Override
    public ItemQuadro decorarInicialmente(Cartao cartao) {
        return new ComPrioridade(cartao, Prioridade.ALTA);
    }
}
