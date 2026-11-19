package br.com.kanban.service;

import br.com.kanban.domain.Coluna;

public class ColunaCheiaException extends RuntimeException {

    public ColunaCheiaException(Coluna coluna, int limite) {
        super("A coluna " + coluna + " está cheia (limite de " + limite + " cartões)");
    }
}