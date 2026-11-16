package br.com.kanban.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.ExtraAplicado;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.comportamental.MovimentoCartaoEvento;
import br.com.kanban.patterns.comportamental.ObservadorCartao;
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
    private final List<ObservadorCartao> observadores = new CopyOnWriteArrayList<>();

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

    // Observer: o serviço só conhece a interface, não os observadores concretos
    public void adicionarObservador(ObservadorCartao observador) {
        observadores.add(observador);
    }

    public void removerObservador(ObservadorCartao observador) {
        observadores.remove(observador);
    }

    private void notificar(MovimentoCartaoEvento evento) {
        observadores.forEach(o -> o.aoMoverCartao(evento));
    }

    public Cartao mover(long id, Coluna destino) {
        Cartao cartao = buscar(id);
        Coluna origem = cartao.getColuna();
        cartao.setColuna(destino);
        Cartao salvo = repositorio.save(cartao);
        if (origem != destino) {
            notificar(new MovimentoCartaoEvento(salvo, origem, destino, LocalDateTime.now()));
        }
        return salvo;
    }

    public void remover(long id) {
        repositorio.delete(buscar(id));
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