package br.com.fatesg.livraria.dto;

import br.com.fatesg.livraria.domain.model.PerfilUsuario;
import br.com.fatesg.livraria.domain.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,
        String perfilDescricao,
        boolean ativo,
        LocalDateTime dataCadastro
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getPerfil().getDescricao(),
                usuario.isAtivo(),
                usuario.getDataCadastro()
        );
    }
}
