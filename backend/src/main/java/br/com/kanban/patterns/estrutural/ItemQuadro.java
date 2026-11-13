package br.com.kanban.patterns.estrutural;

import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.Extras;
import br.com.kanban.domain.TipoCartao;

public interface ItemQuadro {

    long getId();

    TipoCartao getTipo();

    String getTitulo();

    String getDescricao();

    String getDetalhe();

    Coluna getColuna();

    Extras getExtras();
}
