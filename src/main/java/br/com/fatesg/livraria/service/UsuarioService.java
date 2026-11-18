package br.com.fatesg.livraria.service;

import br.com.fatesg.livraria.dto.UsuarioAtualizacaoRequest;
import br.com.fatesg.livraria.dto.UsuarioCadastroRequest;
import br.com.fatesg.livraria.dto.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listar();

    UsuarioResponse buscarPorId(Long id);

    UsuarioResponse cadastrar(UsuarioCadastroRequest request);

    UsuarioResponse atualizar(Long id, UsuarioAtualizacaoRequest request);

    void excluir(Long id, Long idUsuarioLogado);
}
