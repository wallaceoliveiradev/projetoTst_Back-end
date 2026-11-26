package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.NotificacaoAdminResponse;
import br.com.projetotst_api.entity.NotificacaoAdmin;
import br.com.projetotst_api.repository.NotificacaoAdminRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificacoes/admin/bloqueios")
public class NotificacaoAdminController {
    private final NotificacaoAdminRepository repository;

    public NotificacaoAdminController(NotificacaoAdminRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<NotificacaoAdminResponse> listar() {
        return repository.findAllByOrderByCriadaEmDesc().stream()
                .map(NotificacaoAdminResponse::from).toList();
    }

    @PatchMapping("/{id}/lida")
    public NotificacaoAdminResponse marcarComoLida(@PathVariable Long id) {
        NotificacaoAdmin notificacao = repository.findById(id).orElseThrow();
        notificacao.setLida(true);
        return NotificacaoAdminResponse.from(repository.save(notificacao));
    }
}
