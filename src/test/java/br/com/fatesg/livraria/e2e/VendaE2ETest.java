package br.com.fatesg.livraria.e2e;

import br.com.fatesg.livraria.e2e.pages.LivrosPage;
import br.com.fatesg.livraria.e2e.pages.VendasPage;
import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("E2E - Fluxo de vendas pela interface")
class VendaE2ETest extends E2ETestBase {

    @Test
    @DisplayName("Deve cadastrar um livro, vendê-lo e refletir a baixa e a devolução do estoque")
    void deveRegistrarVendaEAtualizarEstoque() {
        String titulo = "Domain-Driven Design " + System.currentTimeMillis();
        String cliente = "Cliente Selenium " + System.currentTimeMillis();
        entrarComoAdministrador();

        LivrosPage livrosPage = new LivrosPage(driver, urlBase()).aguardarCarregamento();
        livrosPage.preencherLivro(titulo, "Eric Evans", DadosTeste.gerarIsbnUnico(), "TECNOLOGIA",
                        "150.00", "5", "2003-08-30")
                .salvar();
        assertTrue(livrosPage.exibiuMensagemDeSucesso("Livro cadastrado com sucesso!"));

        VendasPage vendasPage = livrosPage.irParaVendas();
        vendasPage.preencherVenda(titulo, cliente, "2", "PIX");
        assertEquals("Total previsto: R$ 300,00", vendasPage.totalPrevisto().replace('\u00A0', ' '));

        vendasPage.salvar();

        assertTrue(vendasPage.exibiuMensagemDeSucesso("Venda cadastrada com sucesso!"));
        WebElement linhaVenda = vendasPage.aguardarLinhaDaTabela(cliente);
        assertAll(
                () -> assertTrue(linhaVenda.getText().contains(titulo)),
                () -> assertTrue(linhaVenda.getText().contains("R$ 300,00")),
                () -> assertTrue(linhaVenda.getText().contains("Pix"))
        );
        assertTrue(vendasPage.textoOpcaoDoLivro(titulo).contains("estoque: 3"));

        vendasPage.excluir(cliente);

        assertTrue(vendasPage.exibiuMensagemDeSucesso("Venda excluída com sucesso!"));
        vendasPage.aguardarRemocaoDaLinha(cliente);
        assertTrue(vendasPage.textoOpcaoDoLivro(titulo).contains("estoque: 5"));
    }
}
