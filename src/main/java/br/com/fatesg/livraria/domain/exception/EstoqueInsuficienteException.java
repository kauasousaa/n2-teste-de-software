package br.com.fatesg.livraria.domain.exception;

public class EstoqueInsuficienteException extends RegraNegocioException {

    public EstoqueInsuficienteException(String tituloLivro, int quantidadeDisponivel, int quantidadeSolicitada) {
        super("Estoque insuficiente para o livro '%s': disponível %d, solicitado %d."
                .formatted(tituloLivro, quantidadeDisponivel, quantidadeSolicitada));
    }
}
