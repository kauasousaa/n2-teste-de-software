package br.com.fatesg.livraria.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public abstract class PaginaBase {

    private static final Duration TEMPO_ESPERA = Duration.ofSeconds(10);
    private static final By MENSAGEM_VISIVEL = By.cssSelector("#mensagem.visivel");

    protected final WebDriver driver;
    protected final WebDriverWait espera;
    protected final String urlBase;

    protected PaginaBase(WebDriver driver, String urlBase) {
        this.driver = driver;
        this.urlBase = urlBase;
        this.espera = new WebDriverWait(driver, TEMPO_ESPERA);
    }

    protected WebElement aguardarVisivel(By seletor) {
        return espera.until(ExpectedConditions.visibilityOfElementLocated(seletor));
    }

    protected void preencher(String idCampo, String valor) {
        WebElement campo = aguardarVisivel(By.id(idCampo));
        campo.clear();
        campo.sendKeys(valor);
    }

    /**
     * Campos do tipo date têm formato de digitação dependente do idioma do navegador,
     * por isso o valor ISO é atribuído diretamente.
     */
    protected void definirData(String idCampo, String dataIso) {
        WebElement campo = aguardarVisivel(By.id(idCampo));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", campo, dataIso);
    }

    protected void selecionarPorValor(String idCampo, String valor) {
        new Select(aguardarVisivel(By.id(idCampo))).selectByValue(valor);
    }

    protected void selecionarPorTextoParcial(String idCampo, String trechoTexto) {
        By opcao = By.xpath("//select[@id='%s']/option[contains(., '%s')]".formatted(idCampo, trechoTexto));
        espera.until(ExpectedConditions.presenceOfElementLocated(opcao)).click();
    }

    protected void clicar(String idElemento) {
        espera.until(ExpectedConditions.elementToBeClickable(By.id(idElemento))).click();
    }

    protected void confirmarAlerta() {
        espera.until(ExpectedConditions.alertIsPresent()).accept();
    }

    /**
     * A mensagem de uma ação anterior pode continuar visível, por isso a espera é pelo texto esperado.
     */
    public String aguardarMensagem(String trechoEsperado) {
        espera.until(ExpectedConditions.textToBePresentInElementLocated(MENSAGEM_VISIVEL, trechoEsperado));
        return driver.findElement(MENSAGEM_VISIVEL).getText();
    }

    public boolean exibiuMensagemDeSucesso(String textoEsperado) {
        return exibiuMensagem(textoEsperado, "sucesso");
    }

    public boolean exibiuMensagemDeErro(String textoEsperado) {
        return exibiuMensagem(textoEsperado, "erro");
    }

    private boolean exibiuMensagem(String textoEsperado, String tipo) {
        String texto = aguardarMensagem(textoEsperado);
        String classes = driver.findElement(MENSAGEM_VISIVEL).getAttribute("class");
        return texto.equals(textoEsperado) && classes.contains(tipo);
    }

    public Optional<WebElement> buscarLinhaDaTabela(String textoNaLinha) {
        List<WebElement> linhas = driver.findElements(By.cssSelector("#tabela tbody tr"));
        return linhas.stream().filter(linha -> linha.getText().contains(textoNaLinha)).findFirst();
    }

    public WebElement aguardarLinhaDaTabela(String textoNaLinha) {
        return espera.until(navegador -> buscarLinhaDaTabela(textoNaLinha).orElse(null));
    }

    public void aguardarRemocaoDaLinha(String textoNaLinha) {
        espera.until(navegador -> buscarLinhaDaTabela(textoNaLinha).isEmpty());
    }

    protected void clicarAcaoDaLinha(String textoNaLinha, String classeBotao) {
        aguardarLinhaDaTabela(textoNaLinha).findElement(By.className(classeBotao)).click();
    }
}
