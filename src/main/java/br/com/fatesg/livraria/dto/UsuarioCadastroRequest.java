package br.com.fatesg.livraria.dto;

import br.com.fatesg.livraria.domain.model.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, max = 60, message = "A senha deve ter entre 6 e 60 caracteres.")
        String senha,

        @NotNull(message = "O perfil é obrigatório.")
        PerfilUsuario perfil,

        @NotNull(message = "Informe se o usuário está ativo.")
        Boolean ativo
) {
}
