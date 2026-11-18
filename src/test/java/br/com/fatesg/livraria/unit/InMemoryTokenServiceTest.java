package br.com.fatesg.livraria.unit;

import br.com.fatesg.livraria.service.impl.InMemoryTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@DisplayName("TokenService - controle de sessões")
class InMemoryTokenServiceTest {

    private RelogioAjustavel relogio;
    private InMemoryTokenService tokenService;

    @BeforeEach
    void configurar() {
        relogio = new RelogioAjustavel(Instant.parse("2026-11-18T10:00:00Z"));
        tokenService = new InMemoryTokenService(Duration.ofHours(1), relogio);
    }

    @Test
    @DisplayName("Deve identificar o usuário a partir de um token válido")
    void deveIdentificarUsuarioPeloToken() {
        String token = tokenService.gerarToken(42L);

        assertEquals(Optional.of(42L), tokenService.buscarUsuarioId(token));
    }

    @Test
    @DisplayName("Deve invalidar o token após o tempo de sessão")
    void deveInvalidarTokenExpirado() {
        String token = tokenService.gerarToken(42L);

        relogio.avancar(Duration.ofMinutes(61));

        assertTrue(tokenService.buscarUsuarioId(token).isEmpty());
    }

    @Test
    @DisplayName("Deve invalidar o token revogado no logout")
    void deveInvalidarTokenRevogado() {
        String token = tokenService.gerarToken(42L);

        tokenService.revogar(token);

        assertTrue(tokenService.buscarUsuarioId(token).isEmpty());
    }

    private static final class RelogioAjustavel extends Clock {

        private Instant instanteAtual;

        private RelogioAjustavel(Instant instanteInicial) {
            this.instanteAtual = instanteInicial;
        }

        void avancar(Duration duracao) {
            instanteAtual = instanteAtual.plus(duracao);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zona) {
            return this;
        }

        @Override
        public Instant instant() {
            return instanteAtual;
        }
    }
}
