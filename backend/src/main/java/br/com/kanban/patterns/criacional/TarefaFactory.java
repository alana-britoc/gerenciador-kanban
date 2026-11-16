package br.com.kanban.patterns.criacional;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.TipoCartao;

public class TarefaFactory extends CartaoFactory {

    @Override
    public Cartao criar(String titulo, String descricao, String detalhe) {
        return new Cartao(TipoCartao.TAREFA, titulo, descricao,
                exigirDetalhe(detalhe, "critérios de aceite"));
    }
}
