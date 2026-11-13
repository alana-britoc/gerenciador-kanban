package br.com.kanban.domain;

import br.com.kanban.patterns.estrutural.ItemQuadro;

public class Cartao implements ItemQuadro {

    private final long id;
    private final TipoCartao tipo;
    private String titulo;
    private String descricao;
    private final String detalhe;
    private Coluna coluna;

    public Cartao(long id, TipoCartao tipo, String titulo, String descricao, String detalhe) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.detalhe = detalhe;
        this.coluna = Coluna.A_FAZER;
    }

    @Override public long getId() { return id; }
    @Override public TipoCartao getTipo() { return tipo; }
    @Override public String getTitulo() { return titulo; }
    @Override public String getDescricao() { return descricao; }
    @Override public String getDetalhe() { return detalhe; }
    @Override public Coluna getColuna() { return coluna; }
    @Override public Extras getExtras() { return Extras.NENHUM; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setColuna(Coluna coluna) { this.coluna = coluna; }
}
