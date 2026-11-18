package br.com.fatesg.livraria.api;

import br.com.fatesg.livraria.support.DadosTeste;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static io.restassured.RestAssured.given;

@Tag("api")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ApiTestBase {

    @LocalServerPort
    private int porta;

    protected RequestSpecification especificacaoPublica;
    protected RequestSpecification especificacaoAutenticada;

    @BeforeAll
    static void habilitarLogEmFalhas() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void configurarEspecificacoes() {
        especificacaoPublica = new RequestSpecBuilder()
                .setPort(porta)
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .build();

        especificacaoAutenticada = new RequestSpecBuilder()
                .addRequestSpecification(especificacaoPublica)
                .addHeader("Authorization", "Bearer " + autenticar(DadosTeste.EMAIL_ADMIN, DadosTeste.SENHA_ADMIN))
                .build();
    }

    protected String autenticar(String email, String senha) {
        return given(especificacaoPublica)
                .body(Map.of("email", email, "senha", senha))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected Map<String, Object> novoLivro(String titulo, double preco, int quantidadeEstoque) {
        return Map.of(
                "titulo", titulo,
                "autor", "Autor API",
                "isbn", DadosTeste.gerarIsbnUnico(),
                "categoria", "TECNOLOGIA",
                "preco", preco,
                "quantidadeEstoque", quantidadeEstoque,
                "dataPublicacao", "2022-08-01"
        );
    }

    protected int cadastrarLivro(Map<String, Object> livro) {
        return given(especificacaoAutenticada)
                .body(livro)
                .when()
                .post("/livros")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
