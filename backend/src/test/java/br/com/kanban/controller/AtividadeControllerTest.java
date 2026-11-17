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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AtividadeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void moverParaFeitoApareceNoHistoricoENasNotificacoes() throws Exception {
        mvc.perform(patch("/api/cartoes/1/coluna")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"coluna\":\"FEITO\"}"))
                .andExpect(status().isOk());

        // os observadores guardam em memória e o contexto é reaproveitado entre testes,
        // então procura o registro em vez de assumir a posição
        mvc.perform(get("/api/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.cartaoId == 1 && @.destino == 'FEITO')]").isNotEmpty());

        mvc.perform(get("/api/notificacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.cartaoId == 1)]").isNotEmpty());
    }
}