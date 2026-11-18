package br.com.fatesg.livraria.controller;

import br.com.fatesg.livraria.domain.model.CategoriaLivro;
import br.com.fatesg.livraria.domain.model.FormaPagamento;
import br.com.fatesg.livraria.domain.model.PerfilUsuario;
import br.com.fatesg.livraria.dto.OpcaoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

    @GetMapping("/categorias-livro")
    public List<OpcaoResponse> categoriasLivro() {
        return OpcaoResponse.listarDe(CategoriaLivro.values());
    }

    @GetMapping("/formas-pagamento")
    public List<OpcaoResponse> formasPagamento() {
        return OpcaoResponse.listarDe(FormaPagamento.values());
    }

    @GetMapping("/perfis-usuario")
    public List<OpcaoResponse> perfisUsuario() {
        return OpcaoResponse.listarDe(PerfilUsuario.values());
    }
}
