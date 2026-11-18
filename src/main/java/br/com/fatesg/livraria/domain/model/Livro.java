package br.com.fatesg.livraria.domain.model;

import br.com.fatesg.livraria.domain.exception.EstoqueInsuficienteException;
import br.com.fatesg.livraria.domain.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 100)
    private String autor;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaLivro categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    private LocalDate dataPublicacao;

    protected Livro() {
    }

    public Livro(String titulo, String autor, String isbn, CategoriaLivro categoria,
                 BigDecimal preco, Integer quantidadeEstoque, LocalDate dataPublicacao) {
        atualizarDados(titulo, autor, isbn, categoria, preco, quantidadeEstoque, dataPublicacao);
    }

    public void atualizarDados(String titulo, String autor, String isbn, CategoriaLivro categoria,
                               BigDecimal preco, Integer quantidadeEstoque, LocalDate dataPublicacao) {
        validarPreco(preco);
        validarEstoque(quantidadeEstoque);
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.dataPublicacao = dataPublicacao;
    }

    public void baixarEstoque(int quantidade) {
        if (quantidade > quantidadeEstoque) {
            throw new EstoqueInsuficienteException(titulo, quantidadeEstoque, quantidade);
        }
        quantidadeEstoque -= quantidade;
    }

    public void reporEstoque(int quantidade) {
        quantidadeEstoque += quantidade;
    }

    private static void validarPreco(BigDecimal preco) {
        if (preco == null || preco.signum() <= 0) {
            throw new RegraNegocioException("O preço do livro deve ser maior que zero.");
        }
    }

    private static void validarEstoque(Integer quantidadeEstoque) {
        if (quantidadeEstoque == null || quantidadeEstoque < 0) {
            throw new RegraNegocioException("A quantidade em estoque não pode ser negativa.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public CategoriaLivro getCategoria() {
        return categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }
}
