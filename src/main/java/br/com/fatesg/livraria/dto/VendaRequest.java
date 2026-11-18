package br.com.fatesg.livraria.dto;

import br.com.fatesg.livraria.domain.model.FormaPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record VendaRequest(
        @NotNull(message = "O livro é obrigatório.")
        Long livroId,

        @NotBlank(message = "O nome do cliente é obrigatório.")
        @Size(max = 120, message = "O nome do cliente deve ter no máximo 120 caracteres.")
        String nomeCliente,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @NotNull(message = "A forma de pagamento é obrigatória.")
        FormaPagamento formaPagamento,

        @NotNull(message = "A data da venda é obrigatória.")
        @PastOrPresent(message = "A data da venda não pode estar no futuro.")
        LocalDate dataVenda
) {
}
