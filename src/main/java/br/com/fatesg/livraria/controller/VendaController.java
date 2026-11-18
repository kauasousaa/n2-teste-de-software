package br.com.fatesg.livraria.controller;

import br.com.fatesg.livraria.dto.VendaRequest;
import br.com.fatesg.livraria.dto.VendaResponse;
import br.com.fatesg.livraria.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    public List<VendaResponse> listar() {
        return vendaService.listar();
    }

    @GetMapping("/{id}")
    public VendaResponse buscarPorId(@PathVariable Long id) {
        return vendaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<VendaResponse> registrar(@Valid @RequestBody VendaRequest request) {
        VendaResponse venda = vendaService.registrar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(venda.id()).toUri();
        return ResponseEntity.created(localizacao).body(venda);
    }

    @PutMapping("/{id}")
    public VendaResponse atualizar(@PathVariable Long id, @Valid @RequestBody VendaRequest request) {
        return vendaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        vendaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
