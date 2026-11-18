package br.com.fatesg.livraria.service.impl;

import br.com.fatesg.livraria.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryTokenService implements TokenService {

    private final Map<String, Sessao> sessoes = new ConcurrentHashMap<>();
    private final Duration duracaoSessao;
    private final Clock clock;

    public InMemoryTokenService(@Value("${livraria.auth.duracao-sessao:PT8H}") Duration duracaoSessao,
                                Clock clock) {
        this.duracaoSessao = duracaoSessao;
        this.clock = clock;
    }

    @Override
    public String gerarToken(Long usuarioId) {
        String token = UUID.randomUUID().toString();
        sessoes.put(token, new Sessao(usuarioId, clock.instant().plus(duracaoSessao)));
        return token;
    }

    @Override
    public Optional<Long> buscarUsuarioId(String token) {
        if (token == null) {
            return Optional.empty();
        }
        Sessao sessao = sessoes.get(token);
        if (sessao == null) {
            return Optional.empty();
        }
        if (sessao.expiradaEm(clock.instant())) {
            sessoes.remove(token);
            return Optional.empty();
        }
        return Optional.of(sessao.usuarioId());
    }

    @Override
    public void revogar(String token) {
        if (token != null) {
            sessoes.remove(token);
        }
    }

    private record Sessao(Long usuarioId, Instant expiraEm) {

        boolean expiradaEm(Instant instante) {
            return instante.isAfter(expiraEm);
        }
    }
}
