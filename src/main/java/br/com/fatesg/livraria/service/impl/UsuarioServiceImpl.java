package br.com.fatesg.livraria.service.impl;

import br.com.fatesg.livraria.domain.exception.ConflitoException;
import br.com.fatesg.livraria.domain.exception.RecursoNaoEncontradoException;
import br.com.fatesg.livraria.domain.exception.RegraNegocioException;
import br.com.fatesg.livraria.domain.model.Usuario;
import br.com.fatesg.livraria.dto.UsuarioAtualizacaoRequest;
import br.com.fatesg.livraria.dto.UsuarioCadastroRequest;
import br.com.fatesg.livraria.dto.UsuarioResponse;
import br.com.fatesg.livraria.repository.UsuarioRepository;
import br.com.fatesg.livraria.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByOrderByNomeAsc().stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    @Override
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.from(buscarEntidade(id));
    }

    @Override
    @Transactional
    public UsuarioResponse cadastrar(UsuarioCadastroRequest request) {
        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw emailDuplicado(email);
        }
        Usuario usuario = new Usuario(
                request.nome().trim(),
                email,
                passwordEncoder.encode(request.senha()),
                request.perfil(),
                request.ativo()
        );
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioAtualizacaoRequest request) {
        Usuario usuario = buscarEntidade(id);
        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw emailDuplicado(email);
        }
        usuario.atualizarDados(request.nome().trim(), email, request.perfil(), request.ativo());

        if (request.possuiNovaSenha()) {
            validarTamanhoSenha(request.senha());
            usuario.alterarSenha(passwordEncoder.encode(request.senha()));
        }
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void excluir(Long id, Long idUsuarioLogado) {
        if (id.equals(idUsuarioLogado)) {
            throw new RegraNegocioException("Não é permitido excluir o próprio usuário logado.");
        }
        usuarioRepository.delete(buscarEntidade(id));
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    private static void validarTamanhoSenha(String senha) {
        if (senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraNegocioException(
                    "A senha deve ter no mínimo %d caracteres.".formatted(TAMANHO_MINIMO_SENHA));
        }
    }

    private static String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static ConflitoException emailDuplicado(String email) {
        return new ConflitoException("Já existe um usuário cadastrado com o e-mail %s.".formatted(email));
    }
}
