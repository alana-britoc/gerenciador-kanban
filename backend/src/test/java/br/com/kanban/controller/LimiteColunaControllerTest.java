package br.com.kanban.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.service.CartaoService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LimiteColunaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CartaoService service;

    @AfterEach
    void tirarLimite() {
        service.definirLimite(Coluna.FAZENDO, 0);
    }

    private long novoCartao(String titulo) {
        return service.criar(TipoCartao.TAREFA, titulo, "", "detalhe").getId();
    }

    private ResultActions moverParaFazendo(long id) throws Exception {
        return mvc.perform(patch("/api/cartoes/" + id + "/coluna")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coluna\":\"FAZENDO\"}"));
    }

    @Test
    void colunaCheiaDevolve409ComMensagemClara() throws Exception {
        service.definirLimite(Coluna.FAZENDO, 1);
        long a = novoCartao("a");
        long b = novoCartao("b");

        moverParaFazendo(a).andExpect(status().isOk());
        moverParaFazendo(b)
                .andExpect(status().isConflict())
                .andExpect(content().string(Matchers.containsString("FAZENDO")));
    }

    @Test
    void colunasDevolveContadorELimite() throws Exception {
        service.definirLimite(Coluna.FAZENDO, 1);
        moverParaFazendo(novoCartao("a")).andExpect(status().isOk());

        mvc.perform(get("/api/colunas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].coluna").value("FAZENDO"))
                .andExpect(jsonPath("$[1].quantidade").value(1))
                .andExpect(jsonPath("$[1].limite").value(1));
    }
}