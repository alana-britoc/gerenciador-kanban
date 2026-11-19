package br.com.kanban.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.kanban.patterns.estrutural.ItemQuadro;
import br.com.kanban.patterns.estrutural.TipoExtra;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;

@Entity
public class Cartao implements ItemQuadro {

    public static final int TAMANHO_TITULO = 255;
    public static final int TAMANHO_TEXTO = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCartao tipo;

    @Column(nullable = false, length = TAMANHO_TITULO)
    private String titulo;

    @Column(length = TAMANHO_TEXTO)
    private String descricao;

    @Column(nullable = false, length = TAMANHO_TEXTO)
    private String detalhe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Coluna coluna;

    // Os decoradores não são salvos: o banco guarda os extras na ordem em que
    // foram aplicados, e o CartaoService remonta a pilha de decoradores ao ler.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cartao_extra", joinColumns = @JoinColumn(name = "cartao_id"))
    @OrderColumn(name = "ordem")
    private List<ExtraAplicado> extrasAplicados = new ArrayList<>();

    /** Exigido pelo JPA. */
    protected Cartao() {
    }

    public Cartao(TipoCartao tipo, String titulo, String descricao, String detalhe) {
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

    // sem prefixo "get" para não aparecer no JSON do cartão
    public List<ExtraAplicado> extrasAplicados() {
        return Collections.unmodifiableList(extrasAplicados);
    }

    public void registrarExtra(TipoExtra tipo, String valor) {
        extrasAplicados.add(new ExtraAplicado(tipo, valor));
    }

    public void editarTextos(String titulo, String descricao, String detalhe) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.detalhe = detalhe;
    }

    public boolean removerExtras(TipoExtra tipo, String valor) {
        return extrasAplicados.removeIf(extra -> extra.tipo() == tipo && (valor == null || extra.valor().equals(valor)));
    }
}
