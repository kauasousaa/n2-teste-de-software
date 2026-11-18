package br.com.fatesg.livraria.service.impl;

import br.com.fatesg.livraria.domain.exception.ConflitoException;
import br.com.fatesg.livraria.domain.exception.RecursoNaoEncontradoException;
import br.com.fatesg.livraria.domain.model.Livro;
import br.com.fatesg.livraria.dto.LivroRequest;
import br.com.fatesg.livraria.dto.LivroResponse;
import br.com.fatesg.livraria.repository.LivroRepository;
import br.com.fatesg.livraria.repository.VendaRepository;
import br.com.fatesg.livraria.service.LivroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LivroServiceImpl implements LivroService {

    private static final String RECURSO = "Livro";

    private final LivroRepository livroRepository;
    private final VendaRepository vendaRepository;

    public LivroServiceImpl(LivroRepository livroRepository, VendaRepository vendaRepository) {
        this.livroRepository = livroRepository;
        this.vendaRepository = vendaRepository;
    }

    @Override
    public List<LivroResponse> listar(String termoBusca) {
        List<Livro> livros = termoBusca == null || termoBusca.isBlank()
                ? livroRepository.findAllByOrderByTituloAsc()
                : livroRepository.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrderByTituloAsc(
                        termoBusca.trim(), termoBusca.trim());
        return livros.stream().map(LivroResponse::from).toList();
    }

    @Override
    public LivroResponse buscarPorId(Long id) {
        return LivroResponse.from(buscarEntidade(id));
    }

    @Override
    @Transactional
    public LivroResponse cadastrar(LivroRequest request) {
        if (livroRepository.existsByIsbn(request.isbn())) {
            throw isbnDuplicado(request.isbn());
        }
        Livro livro = new Livro(
                request.titulo().trim(),
                request.autor().trim(),
                request.isbn(),
                request.categoria(),
                request.preco(),
                request.quantidadeEstoque(),
                request.dataPublicacao()
        );
        return LivroResponse.from(livroRepository.save(livro));
    }

    @Override
    @Transactional
    public LivroResponse atualizar(Long id, LivroRequest request) {
        Livro livro = buscarEntidade(id);
        if (livroRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw isbnDuplicado(request.isbn());
        }
        livro.atualizarDados(
                request.titulo().trim(),
                request.autor().trim(),
                request.isbn(),
                request.categoria(),
                request.preco(),
                request.quantidadeEstoque(),
                request.dataPublicacao()
        );
        return LivroResponse.from(livroRepository.save(livro));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Livro livro = buscarEntidade(id);
        if (vendaRepository.existsByLivroId(id)) {
            throw new ConflitoException("Não é possível excluir o livro '%s' pois ele possui vendas registradas."
                    .formatted(livro.getTitulo()));
        }
        livroRepository.delete(livro);
    }

    private Livro buscarEntidade(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(RECURSO, id));
    }

    private static ConflitoException isbnDuplicado(String isbn) {
        return new ConflitoException("Já existe um livro cadastrado com o ISBN %s.".formatted(isbn));
    }
}
