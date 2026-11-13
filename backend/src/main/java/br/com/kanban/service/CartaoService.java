package br.com.kanban.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.criacional.BugFactory;
import br.com.kanban.patterns.criacional.CartaoFactory;
import br.com.kanban.patterns.criacional.MelhoriaFactory;
import br.com.kanban.patterns.criacional.TarefaFactory;

// guarda tudo em memoria por enquanto
@Service
public class CartaoService {

    private final List<Cartao> cartoes = new ArrayList<>();
    private final AtomicLong proximoId = new AtomicLong(1);
    private final CartaoFactory tarefaFactory = new TarefaFactory();
    private final CartaoFactory bugFactory = new BugFactory();
    private final CartaoFactory melhoriaFactory = new MelhoriaFactory();

    public CartaoService() {
        criar(TipoCartao.TAREFA, "Configurar o projeto", "Back em Spring Boot e front em React",
                "API e quadro funcionando");
        criar(TipoCartao.BUG, "Cartão não muda de coluna", "Exemplo de bug",
                "Mover o cartão para Fazendo e verificar a coluna");
    }

    public List<Cartao> listar() {
        return List.copyOf(cartoes);
    }
    // Factory Method
    public Cartao criar(TipoCartao tipo, String titulo, String descricao, String detalhe) {
        CartaoFactory factory = switch (tipo) {
            case TAREFA -> tarefaFactory;
            case BUG -> bugFactory;
            case MELHORIA -> melhoriaFactory;
        };
        Cartao cartao = factory.criar(proximoId.get(), titulo, descricao, detalhe);
        proximoId.incrementAndGet();
        cartoes.add(cartao);
        return cartao;
    }

    // TODO #3: avisar os observadores quando o cartao mudar de coluna
    public Cartao mover(long id, Coluna destino) {
        Cartao cartao = buscar(id);
        cartao.setColuna(destino);
        return cartao;
    }

    public void remover(long id) {
        cartoes.remove(buscar(id));
    }

    private Cartao buscar(long id) {
        return cartoes.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("cartão " + id + " não existe"));
    }
}
