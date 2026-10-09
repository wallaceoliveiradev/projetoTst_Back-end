package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.EntregaEpiRequest;
import br.com.projetotst_api.dto.EntregaEpiResponse;
import br.com.projetotst_api.service.EntregaEpiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/epis/entregas")
public class EntregaEpiController {
    private final EntregaEpiService service;

    public EntregaEpiController(EntregaEpiService service) {
        this.service = service;
    }

    @GetMapping
    public List<EntregaEpiResponse> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntregaEpiResponse criar(@Valid @RequestBody EntregaEpiRequest request) {
        return service.criar(request);
    }

    @PutMapping("/{id}")
    public EntregaEpiResponse atualizar(@PathVariable Long id, @Valid @RequestBody EntregaEpiRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
