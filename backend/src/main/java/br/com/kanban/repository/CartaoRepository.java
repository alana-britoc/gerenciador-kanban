package br.com.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.kanban.domain.Cartao;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {
}
