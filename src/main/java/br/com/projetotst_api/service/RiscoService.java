package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.RiscoRequest;
import br.com.projetotst_api.dto.RiscoResponse;
import br.com.projetotst_api.entity.Risco;
import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;
import br.com.projetotst_api.repository.RiscoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class RiscoService {

    private final RiscoRepository repository;

    public RiscoService(RiscoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RiscoResponse> listar(String busca, NivelRisco nivel, SituacaoRisco situacao) {
        String termo = normalizar(busca);
        return repository.findAll().stream()
                .filter(risco -> termo.isEmpty()
                        || normalizar(risco.getSetor()).contains(termo)
                        || normalizar(risco.getRisco()).contains(termo))
                .filter(risco -> nivel == null || risco.getNivel() == nivel)
                .filter(risco -> situacao == null || risco.getSituacao() == situacao)
                .sorted(Comparator.comparing(Risco::getSetor, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Risco::getRisco, String.CASE_INSENSITIVE_ORDER))
                .map(RiscoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RiscoResponse obter(Long id) {
        return RiscoResponse.from(buscarPorId(id));
    }

    @Transactional
    public RiscoResponse criar(RiscoRequest request) {
        Risco risco = new Risco(
                request.setor().trim(),
                request.risco().trim(),
                request.nivel(),
                request.situacao(),
                normalizarIcone(request.icone()));
        return RiscoResponse.from(repository.save(risco));
    }

    @Transactional
    public RiscoResponse atualizar(Long id, RiscoRequest request) {
        Risco risco = buscarPorId(id);
        risco.setSetor(request.setor().trim());
        risco.setRisco(request.risco().trim());
        risco.setNivel(request.nivel());
        risco.setSituacao(request.situacao());
        risco.setIcone(normalizarIcone(request.icone()));
        return RiscoResponse.from(repository.save(risco));
    }

    private Risco buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Risco não encontrado."));
    }

    private String normalizarIcone(String icone) {
        return icone == null || icone.isBlank() ? null : icone.trim();
    }

    private String normalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
