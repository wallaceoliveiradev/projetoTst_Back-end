package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.InspecaoRequest;
import br.com.projetotst_api.dto.InspecaoResponse;
import br.com.projetotst_api.service.InspecaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/api/inspecoes")
public class InspecaoController {

    private final InspecaoService service;

    public InspecaoController(InspecaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<InspecaoResponse> listar(Authentication authentication) {
        return service.listar(authentication.getName());
    }

    @GetMapping("/{id}")
    public InspecaoResponse obter(@PathVariable Long id, Authentication authentication) {
        return service.obter(id, authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InspecaoResponse criar(@Valid @RequestBody InspecaoRequest request,
                                  Authentication authentication) {
        return service.criar(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public InspecaoResponse atualizar(@PathVariable Long id,
                                      @Valid @RequestBody InspecaoRequest request,
                                      Authentication authentication) {
        return service.atualizar(id, request, authentication.getName());
    }
}
