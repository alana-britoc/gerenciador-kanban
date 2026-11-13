package br.com.kanban.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;
import br.com.kanban.domain.TipoCartao;
import br.com.kanban.service.CartaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoController {

    private final CartaoService service;

    public CartaoController(CartaoService service) {
        this.service = service;
    }

    public record NovoCartao(@NotNull TipoCartao tipo, @NotBlank String titulo,
                             String descricao, @NotBlank String detalhe) {}

    public record Movimento(@NotNull Coluna coluna) {}

    @GetMapping
    public List<Cartao> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cartao criar(@Valid @RequestBody NovoCartao novo) {
        return service.criar(novo.tipo(), novo.titulo(), novo.descricao(), novo.detalhe());
    }

    @PatchMapping("/{id}/coluna")
    public Cartao mover(@PathVariable long id, @Valid @RequestBody Movimento movimento) {
        return service.mover(id, movimento.coluna());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable long id) {
        service.remover(id);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String naoEncontrado(NoSuchElementException e) {
        return e.getMessage();
    }
}
