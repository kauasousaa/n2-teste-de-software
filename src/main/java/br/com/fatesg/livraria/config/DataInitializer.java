package br.com.fatesg.livraria.config;

import br.com.fatesg.livraria.domain.model.CategoriaLivro;
import br.com.fatesg.livraria.domain.model.Livro;
import br.com.fatesg.livraria.domain.model.PerfilUsuario;
import br.com.fatesg.livraria.domain.model.Usuario;
import br.com.fatesg.livraria.repository.LivroRepository;
import br.com.fatesg.livraria.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final PasswordEncoder passwordEncoder;
    private final String emailAdmin;
    private final String senhaAdmin;

    public DataInitializer(UsuarioRepository usuarioRepository, LivroRepository livroRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${livraria.admin.email}") String emailAdmin,
                           @Value("${livraria.admin.senha}") String senhaAdmin) {
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailAdmin = emailAdmin;
        this.senhaAdmin = senhaAdmin;
    }

    @Override
    @Transactional
    public void run(String... args) {
        criarAdministradorPadrao();
        criarLivrosDeExemplo();
    }

    private void criarAdministradorPadrao() {
        if (usuarioRepository.existsByEmailIgnoreCase(emailAdmin)) {
            return;
        }
        usuarioRepository.save(new Usuario("Administrador", emailAdmin,
                passwordEncoder.encode(senhaAdmin), PerfilUsuario.ADMINISTRADOR, true));
        log.info("Usuário administrador padrão criado: {}", emailAdmin);
    }

    private void criarLivrosDeExemplo() {
        if (livroRepository.count() > 0) {
            return;
        }
        livroRepository.saveAll(List.of(
                new Livro("Dom Casmurro", "Machado de Assis", "9788594318602", CategoriaLivro.ROMANCE,
                        new BigDecimal("29.90"), 15, LocalDate.of(1899, 1, 1)),
                new Livro("O Hobbit", "J. R. R. Tolkien", "9788595084742", CategoriaLivro.FANTASIA,
                        new BigDecimal("54.90"), 10, LocalDate.of(1937, 9, 21)),
                new Livro("Código Limpo", "Robert C. Martin", "9788576082675", CategoriaLivro.TECNOLOGIA,
                        new BigDecimal("89.90"), 8, LocalDate.of(2009, 1, 1))
        ));
        log.info("Livros de exemplo cadastrados.");
    }
}
