package br.com.fatesg.livraria.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("API de vendas (/api/vendas)")
class VendaApiTest extends ApiTestBase {

    @Test
    @DisplayName("Deve registrar, alterar e excluir uma venda mantendo o estoque do livro consistente")
    void deveRegistrarAlterarEExcluirVendaControlandoEstoque() {
        int livroId = cadastrarLivro(novoLivro("Refatoração", 50.00, 10));

        int vendaId = given(especificacaoAutenticada)
                .body(novaVenda(livroId, 3))
                .when().post("/vendas")
                .then()
                .statusCode(201)
                .body("tituloLivro", equalTo("Refatoração"))
                .body("valorUnitario", equalTo(50.00f))
                .body("valorTotal", equalTo(150.00f))
                .extract().path("id");

        verificarEstoque(livroId, 7);

        given(especificacaoAutenticada)
                .body(novaVenda(livroId, 5))
                .when().put("/vendas/{id}", vendaId)
                .then()
                .statusCode(200)
                .body("quantidade", equalTo(5))
                .body("valorTotal", equalTo(250.00f));

        verificarEstoque(livroId, 5);

        given(especificacaoAutenticada)
                .when().delete("/vendas/{id}", vendaId)
                .then().statusCode(204);

        verificarEstoque(livroId, 10);
    }

    @Test
    @DisplayName("Deve retornar 422 ao tentar vender quantidade maior que o estoque")
    void deveRecusarVendaComEstoqueInsuficiente() {
        int livroId = cadastrarLivro(novoLivro("Livro Escasso", 40.00, 2));

        given(especificacaoAutenticada)
                .body(novaVenda(livroId, 5))
                .when().post("/vendas")
                .then()
                .statusCode(422)
                .body("mensagem", containsString("Estoque insuficiente"));

        verificarEstoque(livroId, 2);
    }

    @Test
    @DisplayName("Deve retornar 409 ao excluir um livro que possui vendas")
    void deveImpedirExclusaoDeLivroComVendas() {
        int livroId = cadastrarLivro(novoLivro("Livro Vendido", 25.00, 5));
        given(especificacaoAutenticada).body(novaVenda(livroId, 1)).post("/vendas").then().statusCode(201);

        given(especificacaoAutenticada)
                .when().delete("/livros/{id}", livroId)
                .then()
                .statusCode(409)
                .body("mensagem", containsString("possui vendas registradas"));
    }

    private Map<String, Object> novaVenda(int livroId, int quantidade) {
        return Map.of(
                "livroId", livroId,
                "nomeCliente", "Cliente API",
                "quantidade", quantidade,
                "formaPagamento", "PIX",
                "dataVenda", LocalDate.now().toString()
        );
    }

    private void verificarEstoque(int livroId, int estoqueEsperado) {
        given(especificacaoAutenticada)
                .when().get("/livros/{id}", livroId)
                .then()
                .statusCode(200)
                .body("quantidadeEstoque", equalTo(estoqueEsperado));
    }
}
