package br.com.kanban.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.ExtraAplicado;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.criacional.BugFactory;
import br.com.kanban.patterns.criacional.CartaoFactory;
import br.com.kanban.patterns.criacional.MelhoriaFactory;
import br.com.kanban.patterns.criacional.TarefaFactory;
import br.com.kanban.patterns.estrutural.ItemQuadro;
import br.com.kanban.patterns.estrutural.TipoExtra;
import br.com.kanban.repository.CartaoRepository;

@Service
@Transactional
public class CartaoService {

    private final CartaoRepository repositorio;
    private final CartaoFactory tarefaFactory = new TarefaFactory();
    private final CartaoFactory bugFactory = new BugFactory();
    private final CartaoFactory melhoriaFactory = new MelhoriaFactory();

    public CartaoService(CartaoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<ItemQuadro> listar() {
        return repositorio.findAll(Sort.by("id")).stream()
                .map(this::decorar)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemQuadro item(long id) {
        return decorar(buscar(id));
    }

    public ItemQuadro adicionarExtra(long id, TipoExtra tipo, String valor) {
        Cartao cartao = buscar(id);
        // aplica antes de registrar, para um valor inválido não ser salvo
        ItemQuadro decorado = tipo.aplicar(decorar(cartao), valor);
        cartao.registrarExtra(tipo, valor.trim());
        repositorio.save(cartao);
        return decorado;
    }

    // Factory Method
    public ItemQuadro criar(TipoCartao tipo, String titulo, String descricao, String detalhe) {
        Cartao cartao = repositorio.save(fabrica(tipo).criar(titulo, descricao, detalhe));
        return decorar(cartao);
    }

    // TODO #3: avisar os observadores quando o cartao mudar de coluna
    public Cartao mover(long id, Coluna destino) {
        Cartao cartao = buscar(id);
        cartao.setColuna(destino);
        return repositorio.save(cartao);
    }

    public void remover(long id) {
        repositorio.delete(buscar(id));
    }

    public ItemQuadro editar(long id, String titulo, String descricao, String detalhe) {
        Cartao cartao = buscar(id);
        cartao.editarTextos(titulo.trim(), descricao == null ? null : descricao.trim(), detalhe.trim());
        return decorar(repositorio.save(cartao));
    }

    public ItemQuadro removerExtra(long id, TipoExtra tipo, String valor) {
        boolean etiqueta = tipo == TipoExtra.ETIQUETA;
        if (etiqueta && (valor == null || valor.isBlank())) {
            throw new IllegalArgumentException("informe qual etiqueta remover");
        }
        Cartao cartao = buscar(id);
        if (!cartao.removerExtras(tipo, etiqueta ? valor.trim() : null)) {
            throw new NoSuchElementException("o cartão não tem esse extra");
        }
        return decorar(repositorio.save(cartao));
    }

    private CartaoFactory fabrica(TipoCartao tipo) {
        return switch (tipo) {
            case TAREFA -> tarefaFactory;
            case BUG -> bugFactory;
            case MELHORIA -> melhoriaFactory;
        };
    }

    // Decorator: remonta a pilha a partir da decoração inicial da fábrica
    // e dos extras salvos, na ordem em que foram aplicados
    private ItemQuadro decorar(Cartao cartao) {
        ItemQuadro item = fabrica(cartao.getTipo()).decorarInicialmente(cartao);
        for (ExtraAplicado extra : cartao.extrasAplicados()) {
            item = extra.tipo().aplicar(item, extra.valor());
        }
        return item;
    }

    private Cartao buscar(long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException("cartão " + id + " não existe"));
    }
}
