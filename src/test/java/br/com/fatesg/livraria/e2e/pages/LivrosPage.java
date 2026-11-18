package br.com.fatesg.livraria.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LivrosPage extends PaginaBase {

    public LivrosPage(WebDriver driver, String urlBase) {
        super(driver, urlBase);
    }

    public LivrosPage aguardarCarregamento() {
        espera.until(ExpectedConditions.urlContains("/livros.html"));
        aguardarVisivel(By.id("usuarioLogado"));
        espera.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#categoria option[value='ROMANCE']")));
        return this;
    }

    public String nomeUsuarioLogado() {
        return aguardarVisivel(By.id("usuarioLogado")).getText();
    }

    public LivrosPage preencherLivro(String titulo, String autor, String isbn, String categoria,
                                     String preco, String estoque, String dataPublicacaoIso) {
        preencher("titulo", titulo);
        preencher("autor", autor);
        preencher("isbn", isbn);
        selecionarPorValor("categoria", categoria);
        preencher("preco", preco);
        preencher("quantidadeEstoque", estoque);
        definirData("dataPublicacao", dataPublicacaoIso);
        return this;
    }

    public LivrosPage salvar() {
        clicar("btnSalvar");
        return this;
    }

    public LivrosPage editar(String tituloLivro) {
        clicarAcaoDaLinha(tituloLivro, "btnEditar");
        espera.until(ExpectedConditions.textToBe(By.id("tituloFormulario"), "Editar livro"));
        return this;
    }

    public LivrosPage alterarPreco(String novoPreco) {
        preencher("preco", novoPreco);
        return this;
    }

    public LivrosPage excluir(String tituloLivro) {
        clicarAcaoDaLinha(tituloLivro, "btnExcluir");
        confirmarAlerta();
        return this;
    }

    public VendasPage irParaVendas() {
        clicar("menu-vendas");
        return new VendasPage(driver, urlBase).aguardarCarregamento();
    }

    public void sair() {
        clicar("btnSair");
    }
}
