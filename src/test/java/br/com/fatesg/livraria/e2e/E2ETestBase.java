package br.com.fatesg.livraria.e2e;

import br.com.fatesg.livraria.e2e.pages.LoginPage;
import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Por padrão o Chrome roda em modo headless. Para visualizar a execução (ex.: gravação do vídeo),
 * rode os testes com {@code -Dselenium.headless=false}.
 */
@Tag("e2e")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class E2ETestBase {

    @LocalServerPort
    private int porta;

    protected WebDriver driver;

    @BeforeEach
    void iniciarNavegador() {
        ChromeOptions opcoes = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("selenium.headless", "true"))) {
            opcoes.addArguments("--headless=new");
        }
        opcoes.addArguments("--window-size=1440,900", "--lang=pt-BR", "--disable-search-engine-choice-screen");
        driver = new ChromeDriver(opcoes);
    }

    @AfterEach
    void encerrarNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected String urlBase() {
        return "http://localhost:" + porta;
    }

    protected LoginPage abrirLogin() {
        return new LoginPage(driver, urlBase()).abrir();
    }

    protected void entrarComoAdministrador() {
        abrirLogin().entrarCom(DadosTeste.EMAIL_ADMIN, DadosTeste.SENHA_ADMIN);
    }
}
