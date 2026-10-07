package br.com.projetotst_api.controller;
import br.com.projetotst_api.dto.TreinamentoRequest;
import br.com.projetotst_api.dto.TreinamentoResponse;
import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.repository.TreinamentoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinamentos")
public class TreinamentoController {

    private final TreinamentoRepository treinamentoRepository;

    public TreinamentoController(TreinamentoRepository treinamentoRepository) {
        this.treinamentoRepository = treinamentoRepository;
    }

    @GetMapping
    public ResponseEntity<List<TreinamentoResponse>> listar() {
        List<TreinamentoResponse> treinamentos = treinamentoRepository.findAll().stream()
                .map(TreinamentoResponse::from)
                .toList();
        return ResponseEntity.ok(treinamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreinamentoResponse> consultarPorId(@PathVariable Long id) {
        return treinamentoRepository.findById(id)
                .map(TreinamentoResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TreinamentoResponse> cadastrar(@RequestBody @Valid TreinamentoRequest dto) {
        Treinamento treinamento = new Treinamento(
                dto.getNome(),
                dto.getNorma(),
                dto.getDescricao(),
                dto.getCargaHoraria(),
                dto.getValidade(),
                dto.getParticipantes(),
                dto.getStatus().name());
        Treinamento salvo = treinamentoRepository.save(treinamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(TreinamentoResponse.from(salvo));
    }

    @PutMapping("/{id}")
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
                    Treinamento atualizado = treinamentoRepository.save(treinamento);
                    return ResponseEntity.ok(TreinamentoResponse.from(atualizado));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
