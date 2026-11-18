package br.com.fatesg.livraria.service;

import java.util.Optional;

public interface TokenService {

    String gerarToken(Long usuarioId);

    Optional<Long> buscarUsuarioId(String token);

    void revogar(String token);
}
