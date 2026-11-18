package br.com.fatesg.livraria.dto;

import br.com.fatesg.livraria.domain.model.CategoriaLivro;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LivroRequest(
        @NotBlank(message = "O título é obrigatório.")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
        String titulo,

        @NotBlank(message = "O autor é obrigatório.")
        @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres.")
        String autor,

        @NotBlank(message = "O ISBN é obrigatório.")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "O ISBN deve conter 10 ou 13 dígitos numéricos.")
        String isbn,

        @NotNull(message = "A categoria é obrigatória.")
        CategoriaLivro categoria,

        @NotNull(message = "O preço é obrigatório.")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
        @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 2 casas decimais.")
        BigDecimal preco,

        @NotNull(message = "A quantidade em estoque é obrigatória.")
        @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa.")
        Integer quantidadeEstoque,

        @PastOrPresent(message = "A data de publicação não pode estar no futuro.")
        LocalDate dataPublicacao
) {
}
