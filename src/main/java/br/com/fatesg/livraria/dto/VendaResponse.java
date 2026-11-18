package br.com.fatesg.livraria.dto;

import br.com.fatesg.livraria.domain.model.FormaPagamento;
import br.com.fatesg.livraria.domain.model.Venda;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaResponse(
        Long id,
        Long livroId,
        String tituloLivro,
        String nomeCliente,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        FormaPagamento formaPagamento,
        String formaPagamentoDescricao,
        LocalDate dataVenda
) {

    public static VendaResponse from(Venda venda) {
        return new VendaResponse(
                venda.getId(),
                venda.getLivro().getId(),
                venda.getLivro().getTitulo(),
                venda.getNomeCliente(),
                venda.getQuantidade(),
                venda.getValorUnitario(),
                venda.getValorTotal(),
                venda.getFormaPagamento(),
                venda.getFormaPagamento().getDescricao(),
                venda.getDataVenda()
        );
    }
}
