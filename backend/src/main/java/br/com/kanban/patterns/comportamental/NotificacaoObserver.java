package br.com.kanban.patterns.comportamental;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import br.com.kanban.domain.Coluna;

@Component
public class NotificacaoObserver implements ObservadorCartao {

    public record Notificacao(long cartaoId, String mensagem, LocalDateTime horario) {}

    private final List<Notificacao> notificacoes = new CopyOnWriteArrayList<>();

    @Override
    public void aoMoverCartao(MovimentoCartaoEvento evento) {
        if (evento.colunaDestino() == Coluna.FEITO) {
            notificacoes.add(new Notificacao(
                    evento.cartao().getId(),
                    "Cartão \"" + evento.cartao().getTitulo() + "\" chegou em Feito",
                    evento.horario()));
        }
    }

    public List<Notificacao> notificacoes() {
        return List.copyOf(notificacoes);
    }
}