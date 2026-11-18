package br.com.fatesg.livraria.e2e;

import br.com.fatesg.livraria.e2e.pages.LivrosPage;
import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("E2E - CRUD de livros pela interface")
class LivroE2ETest extends E2ETestBase {

    @Test
    @DisplayName("Deve cadastrar, editar e excluir um livro pela interface")
    void deveCadastrarEditarEExcluirLivro() {
        String titulo = "O Programador Pragmático " + System.currentTimeMillis();
        entrarComoAdministrador();
        LivrosPage livrosPage = new LivrosPage(driver, urlBase()).aguardarCarregamento();

        livrosPage.preencherLivro(titulo, "Andrew Hunt", DadosTeste.gerarIsbnUnico(), "TECNOLOGIA",
                        "99.90", "12", "2019-09-13")
                .salvar();

        assertTrue(livrosPage.exibiuMensagemDeSucesso("Livro cadastrado com sucesso!"));
        WebElement linhaCadastrada = livrosPage.aguardarLinhaDaTabela(titulo);
        assertAll(
                () -> assertTrue(linhaCadastrada.getText().contains("Andrew Hunt")),
                () -> assertTrue(linhaCadastrada.getText().contains("Tecnologia")),
                () -> assertTrue(linhaCadastrada.getText().contains("R$ 99,90")),
                () -> assertTrue(linhaCadastrada.getText().contains("13/09/2019"))
        );

        livrosPage.editar(titulo)
                .alterarPreco("120.50")
                .salvar();

        assertTrue(livrosPage.exibiuMensagemDeSucesso("Livro atualizado com sucesso!"));
        assertTrue(livrosPage.aguardarLinhaDaTabela("R$ 120,50").getText().contains(titulo));

        livrosPage.excluir(titulo);

        assertTrue(livrosPage.exibiuMensagemDeSucesso("Livro excluído com sucesso!"));
        livrosPage.aguardarRemocaoDaLinha(titulo);
    }

    @Test
    @DisplayName("Deve exibir os erros de validação ao salvar um livro incompleto")
    void deveExibirErrosDeValidacao() {
        entrarComoAdministrador();
        LivrosPage livrosPage = new LivrosPage(driver, urlBase()).aguardarCarregamento();

        livrosPage.salvar();

        String mensagem = livrosPage.aguardarMensagem("Dados inválidos.");
        assertAll(
                () -> assertTrue(mensagem.startsWith("Dados inválidos.")),
                () -> assertTrue(mensagem.contains("O título é obrigatório.")),
                () -> assertTrue(mensagem.contains("O ISBN é obrigatório."))
        );
    }
}
