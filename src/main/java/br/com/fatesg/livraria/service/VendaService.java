package br.com.fatesg.livraria.service;

import br.com.fatesg.livraria.dto.VendaRequest;
import br.com.fatesg.livraria.dto.VendaResponse;

import java.util.List;

public interface VendaService {

    List<VendaResponse> listar();

    VendaResponse buscarPorId(Long id);

    VendaResponse registrar(VendaRequest request);

    VendaResponse atualizar(Long id, VendaRequest request);

    void excluir(Long id);
}
