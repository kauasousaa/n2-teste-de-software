package br.com.fatesg.livraria.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class VendasPage extends PaginaBase {

    public VendasPage(WebDriver driver, String urlBase) {
        super(driver, urlBase);
    }

    public VendasPage aguardarCarregamento() {
        espera.until(ExpectedConditions.urlContains("/vendas.html"));
        espera.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#formaPagamento option[value='PIX']")));
        return this;
    }

    public VendasPage preencherVenda(String tituloLivro, String nomeCliente, String quantidade, String formaPagamento) {
        selecionarPorTextoParcial("livroId", tituloLivro);
        preencher("nomeCliente", nomeCliente);
        preencher("quantidade", quantidade);
        selecionarPorValor("formaPagamento", formaPagamento);
        return this;
    }

    public String totalPrevisto() {
        return aguardarVisivel(By.id("valorTotalPrevisto")).getText();
    }

    public VendasPage salvar() {
        clicar("btnSalvar");
        return this;
    }

    public String textoOpcaoDoLivro(String tituloLivro) {
        By opcao = By.xpath("//select[@id='livroId']/option[contains(., '%s')]".formatted(tituloLivro));
        return espera.until(ExpectedConditions.presenceOfElementLocated(opcao)).getText();
    }

    public VendasPage excluir(String nomeCliente) {
        clicarAcaoDaLinha(nomeCliente, "btnExcluir");
        confirmarAlerta();
        return this;
    }
}
