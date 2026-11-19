package br.com.kanban.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import br.com.kanban.domain.Coluna;
import br.com.kanban.service.CartaoService;

@Configuration
public class LimitesConfig {

    public LimitesConfig(CartaoService service, Environment env) {
        for (Coluna coluna : Coluna.values()) {
            Integer limite = env.getProperty("kanban.limite." + coluna.name().toLowerCase(), Integer.class);
            if (limite != null) {
                service.definirLimite(coluna, limite);
            }
        }
    }
}