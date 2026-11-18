package br.com.fatesg.livraria.controller;

import br.com.fatesg.livraria.dto.LoginRequest;
import br.com.fatesg.livraria.dto.LoginResponse;
import br.com.fatesg.livraria.dto.UsuarioResponse;
import br.com.fatesg.livraria.security.AuthInterceptor;
import br.com.fatesg.livraria.security.BearerTokenExtractor;
import br.com.fatesg.livraria.service.AuthService;
import br.com.fatesg.livraria.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.autenticar(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        BearerTokenExtractor.extrair(request).ifPresent(authService::encerrarSessao);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UsuarioResponse usuarioLogado(@RequestAttribute(AuthInterceptor.ATRIBUTO_USUARIO_ID) Long usuarioId) {
        return usuarioService.buscarPorId(usuarioId);
    }
}
