package br.com.kanban.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.kanban.service.CartaoService;

@RestController
@RequestMapping("/api/colunas")
public class ColunaController {

    private final CartaoService service;

    public ColunaController(CartaoService service) {
        this.service = service;
    }

    // [{"coluna":"FAZENDO","quantidade":2,"limite":3}, ...]; limite é null quando não há limite
    @GetMapping
    public List<CartaoService.ResumoColuna> colunas() {
        return service.colunas();
    }
}