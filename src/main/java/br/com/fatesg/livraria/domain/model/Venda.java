package br.com.fatesg.livraria.domain.model;

import br.com.fatesg.livraria.domain.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(nullable = false, length = 120)
    private String nomeCliente;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormaPagamento formaPagamento;

    @Column(nullable = false)
    private LocalDate dataVenda;

    protected Venda() {
    }

    public Venda(Livro livro, String nomeCliente, Integer quantidade,
                 FormaPagamento formaPagamento, LocalDate dataVenda) {
        alterar(livro, nomeCliente, quantidade, formaPagamento, dataVenda);
    }

    /**
     * A baixa de estoque é responsabilidade do chamador. O preço do livro é congelado na venda
     * e só é recalculado quando o livro vendido é trocado.
     */
    public void alterar(Livro livro, String nomeCliente, Integer quantidade,
                        FormaPagamento formaPagamento, LocalDate dataVenda) {
        validarQuantidade(quantidade);
        if (this.livro != livro) {
            this.valorUnitario = livro.getPreco();
        }
        this.livro = livro;
        this.nomeCliente = nomeCliente;
        this.quantidade = quantidade;
        this.formaPagamento = formaPagamento;
        this.dataVenda = dataVenda;
        this.valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    private static void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade vendida deve ser maior que zero.");
        }
    }

    public Long getId() {
        return id;
    }

    public Livro getLivro() {
        return livro;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }
}
