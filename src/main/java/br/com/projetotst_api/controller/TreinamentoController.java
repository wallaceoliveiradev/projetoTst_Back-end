package br.com.projetotst_api.controller;
import br.com.projetotst_api.dto.TreinamentoRequest;
import br.com.projetotst_api.dto.TreinamentoResponse;
import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.TreinamentoRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/treinamentos")
public class TreinamentoController {

    private final TreinamentoRepository treinamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public TreinamentoController(TreinamentoRepository treinamentoRepository, UsuarioRepository usuarioRepository) {
        this.treinamentoRepository = treinamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<TreinamentoResponse>> listar(Authentication authentication) {
        List<Treinamento> encontrados = isAdministrador(authentication)
                ? treinamentoRepository.findAll()
                : treinamentoRepository.findDistinctByColaboradoresEmailIgnoreCase(authentication.getName());
        List<TreinamentoResponse> treinamentos = encontrados.stream()
                .map(TreinamentoResponse::from)
                .toList();
        return ResponseEntity.ok(treinamentos);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<TreinamentoResponse> consultarPorId(@PathVariable Long id,
                                                               Authentication authentication) {
        return treinamentoRepository.findById(id)
                .filter(treinamento -> isAdministrador(authentication)
                        || treinamento.getColaboradores().stream()
                        .anyMatch(colaborador -> colaborador.getEmail().equalsIgnoreCase(authentication.getName())))
                .map(TreinamentoResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TreinamentoResponse> cadastrar(@RequestBody @Valid TreinamentoRequest dto) {
        Treinamento treinamento = new Treinamento(
                dto.getNome(),
                dto.getNorma(),
                dto.getDescricao(),
                dto.getCargaHoraria(),
                dto.getValidade(),
                dto.getParticipantes(),
                dto.getStatus().name());
        treinamento.setColaboradores(resolverColaboradores(dto.getUsuarioIds()));
        Treinamento salvo = treinamentoRepository.save(treinamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(TreinamentoResponse.from(salvo));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TreinamentoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TreinamentoRequest dto) {
        return treinamentoRepository.findById(id)
                .map(treinamento -> {
                    treinamento.setNome(dto.getNome());
                    treinamento.setNorma(dto.getNorma());
                    treinamento.setDescricao(dto.getDescricao());
                    treinamento.setCargaHoraria(dto.getCargaHoraria());
                    treinamento.setValidade(dto.getValidade());
                    treinamento.setParticipantes(dto.getParticipantes());
                    treinamento.setStatus(dto.getStatus().name());
                    treinamento.setColaboradores(resolverColaboradores(dto.getUsuarioIds()));
                    Treinamento atualizado = treinamentoRepository.save(treinamento);
                    return ResponseEntity.ok(TreinamentoResponse.from(atualizado));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Set<Usuario> resolverColaboradores(List<Long> usuarioIds) {
        if (usuarioIds == null || usuarioIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Long> idsUnicos = new HashSet<>(usuarioIds);
        List<Usuario> usuarios = usuarioRepository.findAllById(idsUnicos);
        if (usuarios.size() != idsUnicos.size()
                || usuarios.stream().anyMatch(usuario -> !"COLABORADOR".equalsIgnoreCase(usuario.getPerfil()))) {
            throw new IllegalArgumentException("As inscrições devem apontar para colaboradores existentes.");
        }

        return new HashSet<>(usuarios);
    }

    private boolean isAdministrador(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
