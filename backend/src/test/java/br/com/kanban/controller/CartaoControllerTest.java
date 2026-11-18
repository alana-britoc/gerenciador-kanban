package br.com.kanban.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// sobe a aplicação com o banco em memória dos testes; cada teste desfaz o que gravou
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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

    @Test
    void criarBugDevolvePrioridadeInicialAlta() throws Exception {
        mvc.perform(post("/api/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"BUG\",\"titulo\":\"Falha\",\"detalhe\":\"Abrir o quadro\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("BUG"))
                .andExpect(jsonPath("$.extras.prioridade").value("ALTA"));
    }

    @Test
    void tituloLongoDemaisDevolve400() throws Exception {
        String titulo = "a".repeat(256);
        mvc.perform(post("/api/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"TAREFA\",\"titulo\":\"" + titulo + "\",\"detalhe\":\"ok\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editarDevolveOCartaoComOsTextosNovos() throws Exception {
        adicionar("ETIQUETA", "front");

        mvc.perform(patch("/api/cartoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Título novo\",\"descricao\":\"Outra descrição\",\"detalhe\":\"Outro critério\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Título novo"))
                .andExpect(jsonPath("$.descricao").value("Outra descrição"))
                .andExpect(jsonPath("$.detalhe").value("Outro critério"))
                .andExpect(jsonPath("$.extras.etiquetas[0]").value("front"));
    }

    @Test
    void editarComTituloVazioDevolve400() throws Exception {
        mvc.perform(patch("/api/cartoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\" \",\"detalhe\":\"Outro critério\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removerExtraDevolveOCartaoSemOExtra() throws Exception {
        adicionar("PRAZO", "2026-12-01");
        adicionar("ETIQUETA", "front");
        adicionar("ETIQUETA", "back");

        mvc.perform(delete("/api/cartoes/1/extras/PRAZO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extras.prazo").doesNotExist());

        mvc.perform(delete("/api/cartoes/1/extras/ETIQUETA").param("valor", "front"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extras.etiquetas.length()").value(1))
                .andExpect(jsonPath("$.extras.etiquetas[0]").value("back"));
    }

    @Test
    void removerExtraQueNaoExisteDevolve404() throws Exception {
        mvc.perform(delete("/api/cartoes/1/extras/PRAZO"))
                .andExpect(status().isNotFound());
    }

    @Test
    void removerEtiquetaSemDizerQualDevolve400() throws Exception {
        mvc.perform(delete("/api/cartoes/1/extras/ETIQUETA"))
                .andExpect(status().isBadRequest());
    }
}
