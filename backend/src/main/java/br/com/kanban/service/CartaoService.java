package br.com.kanban.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;

// guarda tudo em memoria por enquanto
@Service
public class CartaoService {

    private final List<Cartao> cartoes = new ArrayList<>();
    private final AtomicLong proximoId = new AtomicLong(1);

    public CartaoService() {
        criar(TipoCartao.TAREFA, "Configurar o projeto", "Back em Spring Boot e front em React");
        criar(TipoCartao.BUG, "Cartão não muda de coluna", "Exemplo de bug");
    }

    public List<Cartao> listar() {
        return List.copyOf(cartoes);
    }

    // TODO (#Factory Method): trocar esse new direto pela fabrica de cartoes
    public Cartao criar(TipoCartao tipo, String titulo, String descricao) {
        Cartao cartao = new Cartao(proximoId.getAndIncrement(), tipo, titulo, descricao);
        cartoes.add(cartao);
        return cartao;
    }

    // TODO (#Observer): avisar os observadores quando o cartao mudar de coluna
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
