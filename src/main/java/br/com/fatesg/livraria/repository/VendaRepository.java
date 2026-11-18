package br.com.fatesg.livraria.repository;

import br.com.fatesg.livraria.domain.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    boolean existsByLivroId(Long livroId);

    List<Venda> findAllByOrderByDataVendaDescIdDesc();
}
