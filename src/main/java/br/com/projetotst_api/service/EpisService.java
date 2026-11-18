package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.EpisRequest;
import br.com.projetotst_api.dto.EpisResponse;
import br.com.projetotst_api.dto.EpiCatalogoResponse;
import br.com.projetotst_api.entity.Epis;
import br.com.projetotst_api.repository.EpisRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.LinkedHashMap;

@Service
public class EpisService {

    private final EpisRepository repository;

    public EpisService(EpisRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EpisResponse> listar() {
        return repository.findAll().stream()
                .map(EpisResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EpiCatalogoResponse> listarCatalogo() {
        var porNome = new LinkedHashMap<String, EpiCatalogoResponse>();
        repository.findAll().forEach(epi -> porNome.putIfAbsent(
                epi.getNome().trim().toLowerCase(), EpiCatalogoResponse.from(epi)));
        return List.copyOf(porNome.values());
    }

    @Transactional(readOnly = true)
    public EpisResponse buscar(Long id) {
        return EpisResponse.from(encontrar(id));
    }

    @Transactional
    public EpisResponse cadastrar(EpisRequest request) {
        Epis epis = new Epis(
                request.getNome(),
                request.getCategoria(),
                request.getDataEntrada(),
                request.getQuantidade(),
                request.getValidade(),
                request.getCa()
        );
        return EpisResponse.from(repository.save(epis));
    }

    @Transactional
    public EpisResponse atualizar(Long id, EpisRequest request) {
        Epis epis = encontrar(id);
        epis.setNome(request.getNome());
        epis.setCategoria(request.getCategoria());
        epis.setDataEntrada(request.getDataEntrada());
        epis.setQuantidade(request.getQuantidade());
        epis.setValidade(request.getValidade());
        epis.setCa(request.getCa());
        return EpisResponse.from(repository.save(epis));
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EPI não encontrado.");
        }
        repository.deleteById(id);
    }

    private Epis encontrar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "EPI não encontrado."));
    }
}
