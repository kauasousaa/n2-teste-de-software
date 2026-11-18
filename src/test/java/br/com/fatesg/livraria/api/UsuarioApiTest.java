package br.com.fatesg.livraria.api;

import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@DisplayName("API de usuários (/api/usuarios)")
class UsuarioApiTest extends ApiTestBase {

    @Test
    @DisplayName("Deve cadastrar um usuário que consegue fazer login, atualizá-lo e excluí-lo")
    void deveExecutarCrudDeUsuarioComLogin() {
        String email = DadosTeste.gerarEmailUnico();

        int id = given(especificacaoAutenticada)
                .body(Map.of("nome", "Vendedora Teste", "email", email, "senha", "vendas123",
                        "perfil", "VENDEDOR", "ativo", true))
                .when().post("/usuarios")
                .then()
                .statusCode(201)
                .body("email", equalTo(email))
                .body("perfilDescricao", equalTo("Vendedor"))
                .extract().path("id");

        autenticar(email, "vendas123");

        given(especificacaoAutenticada)
                .body(Map.of("nome", "Vendedora Atualizada", "email", email, "senha", "",
                        "perfil", "VENDEDOR", "ativo", false))
                .when().put("/usuarios/{id}", id)
                .then()
                .statusCode(200)
                .body("nome", equalTo("Vendedora Atualizada"))
                .body("ativo", equalTo(false));

        given(especificacaoPublica)
                .body(Map.of("email", email, "senha", "vendas123"))
                .when().post("/auth/login")
                .then()
                .statusCode(401)
                .body("mensagem", equalTo("Usuário inativo. Procure um administrador."));

        given(especificacaoAutenticada)
                .when().get("/usuarios")
                .then()
                .statusCode(200)
                .body("email", hasItem(email));

        given(especificacaoAutenticada)
                .when().delete("/usuarios/{id}", id)
                .then().statusCode(204);

        given(especificacaoAutenticada)
                .when().get("/usuarios/{id}", id)
                .then().statusCode(404);
    }
}
