package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.HistoricoTreinamentoResponse;
import br.com.projetotst_api.dto.InvalidacaoTreinamentoRequest;
import br.com.projetotst_api.dto.ParametroTreinamentoRequest;
import br.com.projetotst_api.dto.ParametroTreinamentoResponse;
import br.com.projetotst_api.dto.RegistroConclusaoTreinamentoRequest;
import br.com.projetotst_api.entity.HistoricoTreinamento;
import br.com.projetotst_api.entity.StatusHistoricoTreinamento;
import br.com.projetotst_api.service.HistoricoTreinamentoService;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/treinamentos")
public class HistoricoTreinamentoController {

    private final HistoricoTreinamentoService historicoService;

    public HistoricoTreinamentoController(HistoricoTreinamentoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping("/historico")
    public ResponseEntity<List<HistoricoTreinamentoResponse>> listar(Authentication authentication) {
        return ResponseEntity.ok(historicoService.listar(
                authentication.getName(), isAdministrador(authentication)));
    }

    @PostMapping(value = "/historico", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HistoricoTreinamentoResponse> registrar(
            @RequestPart("dados") @Valid RegistroConclusaoTreinamentoRequest dados,
            @RequestPart("certificado") MultipartFile certificado) {
        return ResponseEntity.status(201).body(historicoService.registrar(dados, certificado));
    }

    @PostMapping("/historico/{id}/invalidar")
    public ResponseEntity<HistoricoTreinamentoResponse> invalidar(
            @PathVariable long id, @RequestBody @Valid InvalidacaoTreinamentoRequest request) {
        HistoricoTreinamento registro = historicoService.invalidar(id, request);
        return ResponseEntity.ok(HistoricoTreinamentoResponse.from(
                registro, StatusHistoricoTreinamento.INVALIDADO.name()));
    }

    @GetMapping("/historico/{id}/certificado")
    @Transactional(readOnly = true)
    public ResponseEntity<ByteArrayResource> certificado(@PathVariable long id, Authentication authentication) {
        HistoricoTreinamento registro = historicoService.obter(
                id, authentication.getName(), isAdministrador(authentication));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(registro.getCertificadoContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(registro.getCertificadoNome(), StandardCharsets.UTF_8)
                        .build().toString())
                .body(new ByteArrayResource(registro.getCertificado()));
    }

    @GetMapping("/parametros")
    public ResponseEntity<List<ParametroTreinamentoResponse>> listarParametros() {
        return ResponseEntity.ok(historicoService.listarParametros());
    }

    @PostMapping("/{treinamentoId}/parametros")
    public ResponseEntity<ParametroTreinamentoResponse> criarParametro(
            @PathVariable long treinamentoId, @RequestBody @Valid ParametroTreinamentoRequest request) {
        return ResponseEntity.status(201).body(historicoService.salvarParametro(treinamentoId, null, request));
    }

    @PutMapping("/{treinamentoId}/parametros/{parametroId}")
    public ResponseEntity<ParametroTreinamentoResponse> atualizarParametro(
            @PathVariable long treinamentoId, @PathVariable long parametroId,
            @RequestBody @Valid ParametroTreinamentoRequest request) {
        return ResponseEntity.ok(historicoService.salvarParametro(treinamentoId, parametroId, request));
    }

    @DeleteMapping("/{treinamentoId}/parametros/{parametroId}")
    public ResponseEntity<Void> excluirParametro(
            @PathVariable long treinamentoId, @PathVariable long parametroId) {
        historicoService.excluirParametro(treinamentoId, parametroId);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdministrador(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
