package br.com.kanban.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.kanban.domain.TipoCartao;
import br.com.kanban.patterns.estrutural.ItemQuadro;
import br.com.kanban.service.CartaoService;

// sem @Transactional: os eventos só saem depois do commit, então o movimento precisa ser confirmado
@SpringBootTest
@AutoConfigureMockMvc
class AtividadeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CartaoService service;

    @Test
    void moverParaFeitoApareceNoHistoricoENasNotificacoes() throws Exception {
        ItemQuadro cartao = service.criar(TipoCartao.TAREFA, "Painel de atividade", "", "Aparece no histórico");
        long id = cartao.getId();

        mvc.perform(patch("/api/cartoes/" + id + "/coluna")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"coluna\":\"FEITO\"}"))
                .andExpect(status().isOk());

        // os observadores guardam em memória e o contexto é reaproveitado entre testes,
        // então procura o registro deste cartão em vez de assumir a posição
        mvc.perform(get("/api/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.cartaoId == " + id + " && @.destino == 'FEITO')]").isNotEmpty());

        mvc.perform(get("/api/notificacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.cartaoId == " + id + ")]").isNotEmpty());
    }
}