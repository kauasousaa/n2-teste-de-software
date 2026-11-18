package br.com.fatesg.livraria.dto;

import br.com.fatesg.livraria.domain.model.EnumDescritivo;

import java.util.Arrays;
import java.util.List;

public record OpcaoResponse(String valor, String descricao) {

    public static List<OpcaoResponse> listarDe(EnumDescritivo[] valores) {
        return Arrays.stream(valores)
                .map(valor -> new OpcaoResponse(valor.name(), valor.getDescricao()))
                .toList();
    }
}
