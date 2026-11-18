package br.com.fatesg.livraria.dto;

public record LoginResponse(String token, UsuarioResponse usuario) {
}
