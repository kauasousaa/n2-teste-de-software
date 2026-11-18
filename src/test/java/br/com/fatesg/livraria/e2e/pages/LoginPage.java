package br.com.fatesg.livraria.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends PaginaBase {

    public LoginPage(WebDriver driver, String urlBase) {
        super(driver, urlBase);
    }

    public LoginPage abrir() {
        driver.get(urlBase + "/index.html");
        aguardarVisivel(By.id("formLogin"));
        return this;
    }

    public void entrarCom(String email, String senha) {
        preencher("email", email);
        preencher("senha", senha);
        clicar("btnEntrar");
    }

    public boolean estaAberta() {
        return espera.until(ExpectedConditions.urlContains("/index.html"));
    }
}
