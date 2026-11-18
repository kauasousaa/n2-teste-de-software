package br.com.fatesg.livraria.service.impl;

import br.com.fatesg.livraria.domain.exception.NaoAutenticadoException;
import br.com.fatesg.livraria.domain.model.Usuario;
import br.com.fatesg.livraria.dto.LoginRequest;
import br.com.fatesg.livraria.dto.LoginResponse;
import br.com.fatesg.livraria.dto.UsuarioResponse;
import br.com.fatesg.livraria.repository.UsuarioRepository;
import br.com.fatesg.livraria.service.AuthService;
import br.com.fatesg.livraria.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final String MENSAGEM_CREDENCIAIS_INVALIDAS = "E-mail ou senha inválidos.";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email().trim())
                .filter(encontrado -> passwordEncoder.matches(request.senha(), encontrado.getSenhaHash()))
                .orElseThrow(() -> new NaoAutenticadoException(MENSAGEM_CREDENCIAIS_INVALIDAS));

        if (!usuario.isAtivo()) {
            throw new NaoAutenticadoException("Usuário inativo. Procure um administrador.");
        }

        String token = tokenService.gerarToken(usuario.getId());
        return new LoginResponse(token, UsuarioResponse.from(usuario));
    }

    @Override
    public void encerrarSessao(String token) {
        tokenService.revogar(token);
    }
}
