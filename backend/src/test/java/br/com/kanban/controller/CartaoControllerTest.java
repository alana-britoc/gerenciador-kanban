package br.com.kanban.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.kanban.service.CartaoService;

@WebMvcTest(CartaoController.class)
@Import(CartaoService.class)
class CartaoControllerTest {

    @Autowired
    private MockMvc mvc;

    private void adicionar(String tipo, String valor) throws Exception {
        mvc.perform(post("/api/cartoes/1/extras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"" + tipo + "\",\"valor\":\"" + valor + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void extrasEmpilhadosVoltamNoJson() throws Exception {
        adicionar("PRAZO", "2026-12-01");
        adicionar("PRIORIDADE", "ALTA");
        adicionar("ETIQUETA", "front");

        mvc.perform(patch("/api/cartoes/1/coluna")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"coluna\":\"FAZENDO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coluna").value("FAZENDO"))
                .andExpect(jsonPath("$.extras.prazo").value("2026-12-01"))
                .andExpect(jsonPath("$.extras.prioridade").value("ALTA"))
                .andExpect(jsonPath("$.extras.etiquetas[0]").value("front"));
    }

    @Test
    void extraInvalidoDevolve400() throws Exception {
        mvc.perform(post("/api/cartoes/1/extras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"PRAZO\",\"valor\":\"amanha\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void extraEmCartaoInexistenteDevolve404() throws Exception {
        mvc.perform(post("/api/cartoes/999/extras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"ETIQUETA\",\"valor\":\"front\"}"))
                .andExpect(status().isNotFound());
    }
}
