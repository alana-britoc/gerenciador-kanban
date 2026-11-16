package br.com.kanban.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.com.kanban.domain.TipoCartao;
import br.com.kanban.repository.CartaoRepository;
import br.com.kanban.service.CartaoService;

/** Cria os cartões de exemplo só quando o banco está vazio. */
@Component
public class DadosIniciais implements ApplicationRunner {

    private final CartaoRepository repositorio;
    private final CartaoService service;

    public DadosIniciais(CartaoRepository repositorio, CartaoService service) {
        this.repositorio = repositorio;
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repositorio.count() > 0) {
            return;
        }
        service.criar(TipoCartao.TAREFA, "Configurar o projeto", "Back em Spring Boot e front em React",
                "API e quadro funcionando");
        service.criar(TipoCartao.BUG, "Cartão não muda de coluna", "Exemplo de bug",
                "Mover o cartão para Fazendo e verificar a coluna");
    }
}
