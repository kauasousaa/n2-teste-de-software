package br.com.fatesg.livraria.service;

import br.com.fatesg.livraria.dto.LoginRequest;
import br.com.fatesg.livraria.dto.LoginResponse;

public interface AuthService {

    LoginResponse autenticar(LoginRequest request);

    void encerrarSessao(String token);
}
