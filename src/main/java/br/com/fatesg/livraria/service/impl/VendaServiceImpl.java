package br.com.fatesg.livraria.service.impl;

import br.com.fatesg.livraria.domain.exception.RecursoNaoEncontradoException;
import br.com.fatesg.livraria.domain.model.Livro;
import br.com.fatesg.livraria.domain.model.Venda;
import br.com.fatesg.livraria.dto.VendaRequest;
import br.com.fatesg.livraria.dto.VendaResponse;
import br.com.fatesg.livraria.repository.LivroRepository;
import br.com.fatesg.livraria.repository.VendaRepository;
import br.com.fatesg.livraria.service.VendaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VendaServiceImpl implements VendaService {

    private final VendaRepository vendaRepository;
    private final LivroRepository livroRepository;

    public VendaServiceImpl(VendaRepository vendaRepository, LivroRepository livroRepository) {
        this.vendaRepository = vendaRepository;
        this.livroRepository = livroRepository;
    }

    @Override
    public List<VendaResponse> listar() {
        return vendaRepository.findAllByOrderByDataVendaDescIdDesc().stream()
                .map(VendaResponse::from)
                .toList();
    }

    @Override
    public VendaResponse buscarPorId(Long id) {
        return VendaResponse.from(buscarVenda(id));
    }

    @Override
    @Transactional
    public VendaResponse registrar(VendaRequest request) {
        Livro livro = buscarLivro(request.livroId());
        livro.baixarEstoque(request.quantidade());

        Venda venda = new Venda(
                livro,
                request.nomeCliente().trim(),
                request.quantidade(),
                request.formaPagamento(),
                request.dataVenda()
        );
        return VendaResponse.from(vendaRepository.save(venda));
    }

    @Override
    @Transactional
    public VendaResponse atualizar(Long id, VendaRequest request) {
        Venda venda = buscarVenda(id);
        venda.getLivro().reporEstoque(venda.getQuantidade());

        Livro novoLivro = buscarLivro(request.livroId());
        novoLivro.baixarEstoque(request.quantidade());

        venda.alterar(
                novoLivro,
                request.nomeCliente().trim(),
                request.quantidade(),
                request.formaPagamento(),
                request.dataVenda()
        );
        return VendaResponse.from(vendaRepository.save(venda));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Venda venda = buscarVenda(id);
        venda.getLivro().reporEstoque(venda.getQuantidade());
        vendaRepository.delete(venda);
    }

    private Venda buscarVenda(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Venda", id));
    }

    private Livro buscarLivro(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", id));
    }
}
