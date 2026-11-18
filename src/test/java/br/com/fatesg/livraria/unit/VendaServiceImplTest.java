package br.com.fatesg.livraria.unit;

import br.com.fatesg.livraria.domain.exception.EstoqueInsuficienteException;
import br.com.fatesg.livraria.domain.exception.RecursoNaoEncontradoException;
import br.com.fatesg.livraria.domain.model.FormaPagamento;
import br.com.fatesg.livraria.domain.model.Livro;
import br.com.fatesg.livraria.domain.model.Venda;
import br.com.fatesg.livraria.dto.VendaRequest;
import br.com.fatesg.livraria.dto.VendaResponse;
import br.com.fatesg.livraria.repository.LivroRepository;
import br.com.fatesg.livraria.repository.VendaRepository;
import br.com.fatesg.livraria.service.impl.VendaServiceImpl;
import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("VendaService - regras de negócio de vendas")
class VendaServiceImplTest {

    private static final LocalDate HOJE = LocalDate.now();

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private VendaServiceImpl vendaService;

    @Test
    @DisplayName("Deve registrar a venda calculando o valor total e baixando o estoque do livro")
    void deveRegistrarVendaCalculandoTotalEBaixandoEstoque() {
        Livro livro = DadosTeste.livro(1L, "49.90", 10);
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        VendaResponse venda = vendaService.registrar(new VendaRequest(1L, "Maria Souza", 3, FormaPagamento.PIX, HOJE));

        assertAll(
                () -> assertEquals(new BigDecimal("49.90"), venda.valorUnitario()),
                () -> assertEquals(new BigDecimal("149.70"), venda.valorTotal()),
                () -> assertEquals(7, livro.getQuantidadeEstoque()),
                () -> assertEquals("Maria Souza", venda.nomeCliente())
        );
    }

    @Test
    @DisplayName("Não deve registrar a venda quando o estoque for insuficiente")
    void naoDeveRegistrarVendaComEstoqueInsuficiente() {
        Livro livro = DadosTeste.livro(1L, "30.00", 2);
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        VendaRequest request = new VendaRequest(1L, "João Lima", 5, FormaPagamento.DINHEIRO, HOJE);

        assertThrows(EstoqueInsuficienteException.class, () -> vendaService.registrar(request));
        assertEquals(2, livro.getQuantidadeEstoque());
        verify(vendaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar venda de um livro inexistente")
    void deveLancarExcecaoQuandoLivroNaoExiste() {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        VendaRequest request = new VendaRequest(99L, "Ana", 1, FormaPagamento.BOLETO, HOJE);

        assertThrows(RecursoNaoEncontradoException.class, () -> vendaService.registrar(request));
        verify(vendaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve ajustar o estoque e manter o preço original ao alterar a quantidade da venda")
    void deveAjustarEstoqueAoAtualizarQuantidadeDaVenda() {
        Livro livro = DadosTeste.livro(1L, "20.00", 10);
        livro.baixarEstoque(2);
        Venda vendaExistente = new Venda(livro, "Carlos", 2, FormaPagamento.PIX, HOJE);
        DadosTeste.definirId(vendaExistente, 5L);
        livro.atualizarDados(livro.getTitulo(), livro.getAutor(), livro.getIsbn(), livro.getCategoria(),
                new BigDecimal("25.00"), livro.getQuantidadeEstoque(), livro.getDataPublicacao());

        when(vendaRepository.findById(5L)).thenReturn(Optional.of(vendaExistente));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        VendaResponse venda = vendaService.atualizar(5L,
                new VendaRequest(1L, "Carlos", 5, FormaPagamento.CARTAO_CREDITO, HOJE));

        assertAll(
                () -> assertEquals(5, livro.getQuantidadeEstoque()),
                () -> assertEquals(new BigDecimal("20.00"), venda.valorUnitario()),
                () -> assertEquals(new BigDecimal("100.00"), venda.valorTotal()),
                () -> assertEquals(FormaPagamento.CARTAO_CREDITO, venda.formaPagamento())
        );
    }

    @Test
    @DisplayName("Deve devolver a quantidade ao estoque ao excluir a venda")
    void deveDevolverEstoqueAoExcluirVenda() {
        Livro livro = DadosTeste.livro(1L, "15.00", 10);
        livro.baixarEstoque(4);
        Venda venda = new Venda(livro, "Beatriz", 4, FormaPagamento.CARTAO_DEBITO, HOJE);
        when(vendaRepository.findById(3L)).thenReturn(Optional.of(venda));

        vendaService.excluir(3L);

        assertEquals(10, livro.getQuantidadeEstoque());
        verify(vendaRepository).delete(venda);
    }
}
