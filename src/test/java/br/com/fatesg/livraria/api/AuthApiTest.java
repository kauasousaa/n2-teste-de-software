package br.com.fatesg.livraria.api;

import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@DisplayName("API de autenticação (/api/auth)")
class AuthApiTest extends ApiTestBase {

    @Test
    @DisplayName("POST /auth/login com credenciais válidas retorna 200, token e dados do usuário")
    void deveAutenticarComCredenciaisValidas() {
        given(especificacaoPublica)
                .body(Map.of("email", DadosTeste.EMAIL_ADMIN, "senha", DadosTeste.SENHA_ADMIN))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("usuario.email", equalTo(DadosTeste.EMAIL_ADMIN))
                .body("usuario.perfil", equalTo("ADMINISTRADOR"))
                .body("usuario.senhaHash", nullValue());
    }

    @Test
    @DisplayName("POST /auth/login com senha incorreta retorna 401")
    void deveRecusarLoginComSenhaIncorreta() {
        given(especificacaoPublica)
                .body(Map.of("email", DadosTeste.EMAIL_ADMIN, "senha", "senhaErrada"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("mensagem", equalTo("E-mail ou senha inválidos."));
    }

    @Test
    @DisplayName("Rotas protegidas sem token retornam 401")
    void deveNegarAcessoSemToken() {
        given(especificacaoPublica)
                .when()
                .get("/livros")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /auth/logout invalida o token utilizado")
    void deveInvalidarTokenAposLogout() {
        String token = autenticar(DadosTeste.EMAIL_ADMIN, DadosTeste.SENHA_ADMIN);

        given(especificacaoPublica).header("Authorization", "Bearer " + token)
                .when().post("/auth/logout")
                .then().statusCode(204);

        given(especificacaoPublica).header("Authorization", "Bearer " + token)
                .when().get("/auth/me")
                .then().statusCode(401);
    }
}
