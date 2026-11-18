package br.com.fatesg.livraria.dto;

import br.com.fatesg.livraria.domain.model.CategoriaLivro;
import br.com.fatesg.livraria.domain.model.Livro;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LivroResponse(
        Long id,
        String titulo,
        String autor,
        String isbn,
        CategoriaLivro categoria,
        String categoriaDescricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        LocalDate dataPublicacao
) {

    public static LivroResponse from(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getCategoria(),
                livro.getCategoria().getDescricao(),
                livro.getPreco(),
                livro.getQuantidadeEstoque(),
                livro.getDataPublicacao()
        );
    }
}
