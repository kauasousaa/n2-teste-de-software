package br.com.fatesg.livraria.support;

import br.com.fatesg.livraria.domain.model.CategoriaLivro;
import br.com.fatesg.livraria.domain.model.Livro;
import br.com.fatesg.livraria.dto.LivroRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

public final class DadosTeste {

    public static final String EMAIL_ADMIN = "admin@livraria.com";
    public static final String SENHA_ADMIN = "admin123";

    private static final AtomicLong sequencial = new AtomicLong(System.currentTimeMillis() % 1_000_000_000L);

    private DadosTeste() {
    }

    public static String gerarIsbnUnico() {
        return "978" + String.format("%010d", sequencial.incrementAndGet());
    }

    public static String gerarEmailUnico() {
        return "usuario" + sequencial.incrementAndGet() + "@livraria.com";
    }

    public static Livro livro(Long id, String preco, int quantidadeEstoque) {
        Livro livro = new Livro("Livro de Teste " + id, "Autor de Teste", gerarIsbnUnico(),
                CategoriaLivro.TECNOLOGIA, new BigDecimal(preco), quantidadeEstoque, LocalDate.of(2020, 5, 10));
        definirId(livro, id);
        return livro;
    }

    public static LivroRequest livroRequest(String titulo, String preco, int quantidadeEstoque) {
        return new LivroRequest(titulo, "Autor de Teste", gerarIsbnUnico(), CategoriaLivro.ROMANCE,
                new BigDecimal(preco), quantidadeEstoque, LocalDate.of(2021, 3, 15));
    }

    public static void definirId(Object entidade, Long id) {
        ReflectionTestUtils.setField(entidade, "id", id);
    }
}
