package br.com.fatesg.livraria.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(int status, String mensagem, List<String> detalhes, LocalDateTime timestamp) {

    public static ErroResponse of(int status, String mensagem) {
        return of(status, mensagem, List.of());
    }

    public static ErroResponse of(int status, String mensagem, List<String> detalhes) {
        return new ErroResponse(status, mensagem, detalhes, LocalDateTime.now());
    }
}
