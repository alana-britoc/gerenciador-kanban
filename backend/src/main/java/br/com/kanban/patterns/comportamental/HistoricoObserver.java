package br.com.kanban.patterns.comportamental;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import br.com.kanban.domain.Coluna;

@Component
public class HistoricoObserver implements ObservadorCartao {

    public record Registro(long cartaoId, String titulo, Coluna origem,
                           Coluna destino, LocalDateTime horario) {}

    private final List<Registro> registros = new CopyOnWriteArrayList<>();

    @Override
    public void aoMoverCartao(MovimentoCartaoEvento evento) {
        registros.add(new Registro(
                evento.cartao().getId(),
                evento.cartao().getTitulo(),
                evento.colunaOrigem(),
                evento.colunaDestino(),
                evento.horario()));
    }

    public List<Registro> registros() {
        return List.copyOf(registros);
    }
}