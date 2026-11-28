package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.SolicitacaoEpiRequest;
import br.com.projetotst_api.dto.SolicitacaoEpiResponse;
import br.com.projetotst_api.entity.EntregaEpiItem;
import br.com.projetotst_api.entity.SolicitacaoEpi;
import br.com.projetotst_api.model.TipoSolicitacaoEpi;
import br.com.projetotst_api.repository.EntregaEpiRepository;
import br.com.projetotst_api.repository.SolicitacaoEpiRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitacaoEpiService {
    private final SolicitacaoEpiRepository solicitacoes;
    private final EntregaEpiRepository entregas;
    private final UsuarioRepository usuarios;
    private final Clock clock;

    public SolicitacaoEpiService(SolicitacaoEpiRepository solicitacoes,
                                 EntregaEpiRepository entregas,
                                 UsuarioRepository usuarios, Clock clock) {
        this.solicitacoes = solicitacoes;
        this.entregas = entregas;
        this.usuarios = usuarios;
        this.clock = clock;
    }

    @Transactional
    public SolicitacaoEpiResponse criar(String email, SolicitacaoEpiRequest request) {
        var usuario = usuarios.findByEmailIgnoreCaseForUpdate(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        String nomeEpi = request.epi().trim();
        String motivo = request.motivo().trim();
        if (request.tipo() == TipoSolicitacaoEpi.DEVOLUCAO) {
            int disponivel = entregas.findAllByUsuario_EmailIgnoreCaseOrderByDataEntregaDescIdDesc(email)
                    .stream().flatMap(entrega -> entrega.getItens().stream())
                    .filter(item -> item.getEpi().getNome().equalsIgnoreCase(nomeEpi))
                    .mapToInt(EntregaEpiItem::getQuantidade).sum();
            if (disponivel == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Indisponível");
            }
            if (request.quantidade() > disponivel) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Quantidade Indisponível");
            }
        }
        var criada = solicitacoes.save(new SolicitacaoEpi(usuario, request.tipo(), nomeEpi,
                request.quantidade(), motivo, LocalDateTime.now(clock)));
        return SolicitacaoEpiResponse.from(criada);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoEpiResponse> listarParaAdmin() {
        return solicitacoes.findAllByOrderByCriadaEmDescIdDesc().stream()
                .map(SolicitacaoEpiResponse::from).toList();
    }

    @Transactional
    public SolicitacaoEpiResponse marcarComoVisualizada(Long id) {
        var solicitacao = obter(id);
        solicitacao.setVisualizada(true);
        return SolicitacaoEpiResponse.from(solicitacoes.save(solicitacao));
    }

    @Transactional
    public void excluir(Long id) {
        solicitacoes.delete(obter(id));
    }

    private SolicitacaoEpi obter(Long id) {
        return solicitacoes.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação de EPI não encontrada."));
    }
}
