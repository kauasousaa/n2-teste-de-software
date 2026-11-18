package br.com.fatesg.livraria.security;

import br.com.fatesg.livraria.domain.exception.NaoAutenticadoException;
import br.com.fatesg.livraria.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_USUARIO_ID = "usuarioIdAutenticado";

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        Long usuarioId = BearerTokenExtractor.extrair(request)
                .flatMap(tokenService::buscarUsuarioId)
                .orElseThrow(() -> new NaoAutenticadoException("Sessão inválida ou expirada. Faça login novamente."));

        request.setAttribute(ATRIBUTO_USUARIO_ID, usuarioId);
        return true;
    }
}
