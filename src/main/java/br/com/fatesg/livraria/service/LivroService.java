package br.com.fatesg.livraria.service;

import br.com.fatesg.livraria.dto.LivroRequest;
import br.com.fatesg.livraria.dto.LivroResponse;

import java.util.List;

public interface LivroService {

    List<LivroResponse> listar(String termoBusca);

    LivroResponse buscarPorId(Long id);

    LivroResponse cadastrar(LivroRequest request);

    LivroResponse atualizar(Long id, LivroRequest request);

    void excluir(Long id);
}
