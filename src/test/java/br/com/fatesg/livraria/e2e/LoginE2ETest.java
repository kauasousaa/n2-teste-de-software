package br.com.fatesg.livraria.e2e;

import br.com.fatesg.livraria.e2e.pages.LivrosPage;
import br.com.fatesg.livraria.e2e.pages.LoginPage;
import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("E2E - Tela de login")
class LoginE2ETest extends E2ETestBase {

    @Test
    @DisplayName("Deve autenticar o administrador, exibir o nome no cabeçalho e permitir sair do sistema")
    void deveAutenticarEDesconectarUsuario() {
        entrarComoAdministrador();

        LivrosPage livrosPage = new LivrosPage(driver, urlBase()).aguardarCarregamento();
        assertEquals("Administrador", livrosPage.nomeUsuarioLogado());

        livrosPage.sair();
        assertTrue(new LoginPage(driver, urlBase()).estaAberta());
    }

    @Test
    @DisplayName("Deve exibir mensagem de erro e permanecer na tela de login com senha inválida")
    void deveExibirErroComCredenciaisInvalidas() {
        LoginPage loginPage = abrirLogin();

        loginPage.entrarCom(DadosTeste.EMAIL_ADMIN, "senhaIncorreta");

        assertTrue(loginPage.exibiuMensagemDeErro("E-mail ou senha inválidos."));
        assertTrue(driver.getCurrentUrl().contains("/index.html"));
    }

    @Test
    @DisplayName("Deve redirecionar para o login ao acessar uma página protegida sem sessão")
    void deveRedirecionarParaLoginSemSessao() {
        driver.get(urlBase() + "/vendas.html");

        assertTrue(new LoginPage(driver, urlBase()).estaAberta());
    }
}
