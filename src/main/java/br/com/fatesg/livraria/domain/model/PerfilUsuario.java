package br.com.fatesg.livraria.domain.model;

public enum PerfilUsuario implements EnumDescritivo {
    ADMINISTRADOR("Administrador"),
    VENDEDOR("Vendedor");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
