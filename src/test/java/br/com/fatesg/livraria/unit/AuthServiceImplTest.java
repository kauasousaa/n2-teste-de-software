package br.com.fatesg.livraria.unit;

import br.com.fatesg.livraria.domain.exception.NaoAutenticadoException;
import br.com.fatesg.livraria.domain.model.PerfilUsuario;
import br.com.fatesg.livraria.domain.model.Usuario;
import br.com.fatesg.livraria.dto.LoginRequest;
import br.com.fatesg.livraria.dto.LoginResponse;
import br.com.fatesg.livraria.repository.UsuarioRepository;
import br.com.fatesg.livraria.service.TokenService;
import br.com.fatesg.livraria.service.impl.AuthServiceImpl;
import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - autenticação de usuários")
class AuthServiceImplTest {

    private static final String EMAIL = "vendedor@livraria.com";
    private static final String SENHA = "senha123";
    private static final String HASH = "$2a$10$hashDeTeste";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Deve gerar token ao autenticar com credenciais válidas")
    void deveAutenticarComCredenciaisValidas() {
        Usuario usuario = usuario(true);
        when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(SENHA, HASH)).thenReturn(true);
        when(tokenService.gerarToken(1L)).thenReturn("token-gerado");

        LoginResponse resposta = authService.autenticar(new LoginRequest(EMAIL, SENHA));

        assertAll(
                () -> assertEquals("token-gerado", resposta.token()),
                () -> assertEquals(EMAIL, resposta.usuario().email()),
                () -> assertEquals(PerfilUsuario.VENDEDOR, resposta.usuario().perfil())
        );
    }

    @Test
    @DisplayName("Deve recusar a autenticação quando a senha estiver incorreta")
    void deveRecusarSenhaIncorreta() {
        when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("errada", HASH)).thenReturn(false);

        NaoAutenticadoException excecao = assertThrows(NaoAutenticadoException.class,
                () -> authService.autenticar(new LoginRequest(EMAIL, "errada")));

        assertEquals("E-mail ou senha inválidos.", excecao.getMessage());
        verify(tokenService, never()).gerarToken(anyLong());
    }

    @Test
    @DisplayName("Deve recusar a autenticação de um e-mail não cadastrado")
    void deveRecusarEmailNaoCadastrado() {
        when(usuarioRepository.findByEmailIgnoreCase("naoexiste@livraria.com")).thenReturn(Optional.empty());

        assertThrows(NaoAutenticadoException.class,
                () -> authService.autenticar(new LoginRequest("naoexiste@livraria.com", SENHA)));
        verify(tokenService, never()).gerarToken(anyLong());
    }

    @Test
    @DisplayName("Deve recusar a autenticação de um usuário inativo")
    void deveRecusarUsuarioInativo() {
        when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(usuario(false)));
        when(passwordEncoder.matches(SENHA, HASH)).thenReturn(true);

        NaoAutenticadoException excecao = assertThrows(NaoAutenticadoException.class,
                () -> authService.autenticar(new LoginRequest(EMAIL, SENHA)));

        assertEquals("Usuário inativo. Procure um administrador.", excecao.getMessage());
        verify(tokenService, never()).gerarToken(anyLong());
    }

    private static Usuario usuario(boolean ativo) {
        Usuario usuario = new Usuario("Vendedor", EMAIL, HASH, PerfilUsuario.VENDEDOR, ativo);
        DadosTeste.definirId(usuario, 1L);
        return usuario;
    }
}
