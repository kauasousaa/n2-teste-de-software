package br.com.fatesg.livraria.unit;

import br.com.fatesg.livraria.domain.exception.ConflitoException;
import br.com.fatesg.livraria.domain.exception.RecursoNaoEncontradoException;
import br.com.fatesg.livraria.domain.exception.RegraNegocioException;
import br.com.fatesg.livraria.domain.model.CategoriaLivro;
import br.com.fatesg.livraria.domain.model.Livro;
import br.com.fatesg.livraria.dto.LivroRequest;
import br.com.fatesg.livraria.dto.LivroResponse;
import br.com.fatesg.livraria.repository.LivroRepository;
import br.com.fatesg.livraria.repository.VendaRepository;
import br.com.fatesg.livraria.service.impl.LivroServiceImpl;
import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LivroService - regras de negócio do catálogo")
class LivroServiceImplTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private VendaRepository vendaRepository;

    @InjectMocks
    private LivroServiceImpl livroService;

    @Test
    @DisplayName("Deve cadastrar um livro quando o ISBN ainda não existe")
    void deveCadastrarLivroComIsbnInedito() {
        LivroRequest request = DadosTeste.livroRequest("  Memórias Póstumas  ", "39.90", 12);
        when(livroRepository.existsByIsbn(request.isbn())).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenAnswer(invocacao -> {
            Livro livro = invocacao.getArgument(0);
            DadosTeste.definirId(livro, 10L);
            return livro;
        });

        LivroResponse livro = livroService.cadastrar(request);

        assertAll(
                () -> assertEquals(10L, livro.id()),
                () -> assertEquals("Memórias Póstumas", livro.titulo()),
                () -> assertEquals(new BigDecimal("39.90"), livro.preco()),
                () -> assertEquals(12, livro.quantidadeEstoque())
        );
    }

    @Test
    @DisplayName("Não deve cadastrar um livro com ISBN já existente")
    void naoDeveCadastrarLivroComIsbnDuplicado() {
        LivroRequest request = DadosTeste.livroRequest("Duplicado", "10.00", 1);
        when(livroRepository.existsByIsbn(request.isbn())).thenReturn(true);

        assertThrows(ConflitoException.class, () -> livroService.cadastrar(request));
        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar um livro inexistente")
    void deveLancarExcecaoAoBuscarLivroInexistente() {
        when(livroRepository.findById(404L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException excecao =
                assertThrows(RecursoNaoEncontradoException.class, () -> livroService.buscarPorId(404L));
        assertEquals("Livro com id 404 não encontrado.", excecao.getMessage());
    }

    @Test
    @DisplayName("Não deve excluir um livro que possui vendas registradas")
    void naoDeveExcluirLivroComVendas() {
        Livro livro = DadosTeste.livro(7L, "25.00", 3);
        when(livroRepository.findById(7L)).thenReturn(Optional.of(livro));
        when(vendaRepository.existsByLivroId(7L)).thenReturn(true);

        assertThrows(ConflitoException.class, () -> livroService.excluir(7L));
        verify(livroRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve excluir um livro sem vendas")
    void deveExcluirLivroSemVendas() {
        Livro livro = DadosTeste.livro(8L, "25.00", 3);
        when(livroRepository.findById(8L)).thenReturn(Optional.of(livro));
        when(vendaRepository.existsByLivroId(8L)).thenReturn(false);

        livroService.excluir(8L);

        verify(livroRepository).delete(livro);
    }

    @ParameterizedTest(name = "preço inválido: {0}")
    @ValueSource(strings = {"0", "0.00", "-15.50"})
    @DisplayName("Não deve permitir criar livro com preço menor ou igual a zero")
    void naoDevePermitirLivroComPrecoInvalido(String precoInvalido) {
        BigDecimal preco = new BigDecimal(precoInvalido);

        assertThrows(RegraNegocioException.class, () -> new Livro("Título", "Autor", "9781234567890",
                CategoriaLivro.HISTORIA, preco, 1, LocalDate.of(2000, 1, 1)));
    }
}
