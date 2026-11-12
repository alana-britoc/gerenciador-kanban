package br.com.kanban.domain;

public class Cartao {

    private final long id;
    private final TipoCartao tipo;
    private String titulo;
    private String descricao;
    private Coluna coluna;

    public Cartao(long id, TipoCartao tipo, String titulo, String descricao) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.coluna = Coluna.A_FAZER;
    }

    public long getId() { return id; }
    public TipoCartao getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public Coluna getColuna() { return coluna; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setColuna(Coluna coluna) { this.coluna = coluna; }
}
