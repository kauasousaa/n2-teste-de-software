package br.com.fatesg.livraria.repository;

import br.com.fatesg.livraria.domain.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    List<Livro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrderByTituloAsc(String titulo, String autor);

    List<Livro> findAllByOrderByTituloAsc();
}
