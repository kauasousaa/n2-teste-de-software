package br.com.fatesg.livraria.dto;

import br.com.fatesg.livraria.domain.model.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Quando {@code senha} vier vazia, a senha atual do usuário é mantida.
 */
public record UsuarioAtualizacaoRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres.")
        String email,

        @Size(max = 60, message = "A senha deve ter no máximo 60 caracteres.")
        String senha,

        @NotNull(message = "O perfil é obrigatório.")
        PerfilUsuario perfil,

        @NotNull(message = "Informe se o usuário está ativo.")
        Boolean ativo
) {

    public boolean possuiNovaSenha() {
        return senha != null && !senha.isBlank();
    }
}
