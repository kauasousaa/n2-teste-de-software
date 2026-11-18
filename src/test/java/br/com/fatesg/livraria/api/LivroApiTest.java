package br.com.fatesg.livraria.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.matchesPattern;

@DisplayName("API de livros (/api/livros)")
class LivroApiTest extends ApiTestBase {

    @Test
    @DisplayName("Deve executar o CRUD completo de um livro")
    void deveExecutarCrudCompletoDeLivro() {
        Map<String, Object> livro = novoLivro("Arquitetura Limpa", 79.90, 20);

        int id = given(especificacaoAutenticada)
                .body(livro)
                .when()
                .post("/livros")
                .then()
                .statusCode(201)
                .header("Location", matchesPattern(".*/api/livros/\\d+$"))
                .extract().path("id");

        given(especificacaoAutenticada)
                .when().get("/livros/{id}", id)
                .then()
                .statusCode(200)
                .body("titulo", equalTo("Arquitetura Limpa"))
                .body("categoriaDescricao", equalTo("Tecnologia"))
                .body("preco", equalTo(79.90f))
                .body("quantidadeEstoque", equalTo(20));

        Map<String, Object> livroAtualizado = new HashMap<>(livro);
        livroAtualizado.put("titulo", "Arquitetura Limpa - 2ª edição");
        livroAtualizado.put("preco", 89.90);

        given(especificacaoAutenticada)
                .body(livroAtualizado)
                .when().put("/livros/{id}", id)
                .then()
                .statusCode(200)
                .body("titulo", equalTo("Arquitetura Limpa - 2ª edição"))
                .body("preco", equalTo(89.90f));

        given(especificacaoAutenticada)
                .queryParam("busca", "2ª edição")
                .when().get("/livros")
                .then()
                .statusCode(200)
                .body("id", hasItem(id));

        given(especificacaoAutenticada)
                .when().delete("/livros/{id}", id)
                .then().statusCode(204);

        given(especificacaoAutenticada)
                .when().get("/livros/{id}", id)
                .then()
                .statusCode(404)
                .body("mensagem", equalTo("Livro com id " + id + " não encontrado."));
    }

    @Test
    @DisplayName("Deve retornar 400 com os detalhes de validação ao enviar um livro inválido")
    void deveRetornarErroDeValidacaoParaLivroInvalido() {
        Map<String, Object> livroInvalido = Map.of(
                "titulo", "",
                "autor", "Autor",
                "isbn", "123",
                "categoria", "TECNOLOGIA",
                "preco", -1,
                "quantidadeEstoque", 5
        );

        given(especificacaoAutenticada)
                .body(livroInvalido)
                .when().post("/livros")
                .then()
                .statusCode(400)
                .body("mensagem", equalTo("Dados inválidos."))
                .body("detalhes", hasItems(
                        containsString("titulo"),
                        containsString("isbn"),
                        containsString("preco")));
    }

    @Test
    @DisplayName("Deve persistir datas de publicação históricas sem deslocamento de fuso horário")
    void devePreservarDataDePublicacaoHistorica() {
        Map<String, Object> livro = new HashMap<>(novoLivro("Dom Casmurro", 29.90, 4));
        livro.put("dataPublicacao", "1899-01-01");
        int id = cadastrarLivro(livro);

        given(especificacaoAutenticada)
                .when().get("/livros/{id}", id)
                .then()
                .statusCode(200)
                .body("dataPublicacao", equalTo("1899-01-01"));
    }

    @Test
    @DisplayName("Deve retornar 409 ao cadastrar um livro com ISBN duplicado")
    void deveRetornarConflitoParaIsbnDuplicado() {
        Map<String, Object> livro = novoLivro("Livro Original", 30.0, 3);
        cadastrarLivro(livro);

        given(especificacaoAutenticada)
                .body(livro)
                .when().post("/livros")
                .then()
                .statusCode(409)
                .body("mensagem", containsString(livro.get("isbn").toString()));
    }
}
