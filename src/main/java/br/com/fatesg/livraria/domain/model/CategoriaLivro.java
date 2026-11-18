package br.com.fatesg.livraria.domain.model;

public enum CategoriaLivro implements EnumDescritivo {
    ROMANCE("Romance"),
    FANTASIA("Fantasia"),
    FICCAO_CIENTIFICA("Ficção Científica"),
    TECNOLOGIA("Tecnologia"),
    BIOGRAFIA("Biografia"),
    INFANTIL("Infantil"),
    AUTOAJUDA("Autoajuda"),
    HISTORIA("História");

    private final String descricao;

    CategoriaLivro(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
