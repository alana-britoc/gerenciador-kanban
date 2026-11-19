package br.com.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.kanban.domain.Cartao;
import br.com.kanban.domain.Coluna;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {

    long countByColuna(Coluna coluna);
}