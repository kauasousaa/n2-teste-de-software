package br.com.fatesg.livraria.unit;

import br.com.fatesg.livraria.domain.exception.ConflitoException;
import br.com.fatesg.livraria.domain.exception.RegraNegocioException;
import br.com.fatesg.livraria.domain.model.PerfilUsuario;
import br.com.fatesg.livraria.domain.model.Usuario;
import br.com.fatesg.livraria.dto.UsuarioAtualizacaoRequest;
import br.com.fatesg.livraria.dto.UsuarioCadastroRequest;
import br.com.fatesg.livraria.repository.UsuarioRepository;
import br.com.fatesg.livraria.service.impl.UsuarioServiceImpl;
import br.com.fatesg.livraria.support.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - gestão de usuários")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @Test
    @DisplayName("Deve cadastrar o usuário com e-mail normalizado e senha criptografada")
    void deveCadastrarUsuarioComSenhaCriptografada() {
        when(usuarioRepository.existsByEmailIgnoreCase("novo@livraria.com")).thenReturn(false);
        when(passwordEncoder.encode("segredo1")).thenReturn("hashSeguro");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        usuarioService.cadastrar(new UsuarioCadastroRequest(
                "Novo Usuário", "  NOVO@Livraria.com ", "segredo1", PerfilUsuario.VENDEDOR, true));

        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario salvo = usuarioCaptor.getValue();
        assertAll(
                () -> assertEquals("novo@livraria.com", salvo.getEmail()),
                () -> assertEquals("hashSeguro", salvo.getSenhaHash()),
                () -> assertEquals(PerfilUsuario.VENDEDOR, salvo.getPerfil())
        );
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com e-mail já utilizado")
    void naoDeveCadastrarUsuarioComEmailDuplicado() {
        when(usuarioRepository.existsByEmailIgnoreCase(DadosTeste.EMAIL_ADMIN)).thenReturn(true);

        UsuarioCadastroRequest request = new UsuarioCadastroRequest(
                "Outro Admin", DadosTeste.EMAIL_ADMIN, "segredo1", PerfilUsuario.ADMINISTRADOR, true);

        assertThrows(ConflitoException.class, () -> usuarioService.cadastrar(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve manter a senha atual quando a atualização não informar nova senha")
    void deveManterSenhaAtualQuandoNaoInformada() {
        Usuario usuario = new Usuario("Fulano", "fulano@livraria.com", "hashAntigo", PerfilUsuario.VENDEDOR, true);
        DadosTeste.definirId(usuario, 2L);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmailIgnoreCaseAndIdNot("fulano@livraria.com", 2L)).thenReturn(false);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.atualizar(2L, new UsuarioAtualizacaoRequest(
                "Fulano de Tal", "fulano@livraria.com", "", PerfilUsuario.ADMINISTRADOR, true));

        assertAll(
                () -> assertEquals("hashAntigo", usuario.getSenhaHash()),
                () -> assertEquals("Fulano de Tal", usuario.getNome()),
                () -> assertEquals(PerfilUsuario.ADMINISTRADOR, usuario.getPerfil())
        );
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Não deve permitir que o usuário logado exclua a si mesmo")
    void naoDevePermitirExcluirProprioUsuario() {
        assertThrows(RegraNegocioException.class, () -> usuarioService.excluir(1L, 1L));
        verify(usuarioRepository, never()).delete(any());
    }
}
