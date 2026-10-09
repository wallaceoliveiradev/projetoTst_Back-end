package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.EntregaEpiRequest;
import br.com.projetotst_api.dto.EntregaEpiResponse;
import br.com.projetotst_api.entity.EntregaEpi;
import br.com.projetotst_api.entity.EntregaEpiItem;
import br.com.projetotst_api.entity.Epis;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.EntregaEpiRepository;
import br.com.projetotst_api.repository.EpisRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
public class EntregaEpiService {
    private final EntregaEpiRepository entregas;
    private final EpisRepository epis;
    private final UsuarioRepository usuarios;

    public EntregaEpiService(EntregaEpiRepository entregas, EpisRepository epis,
                             UsuarioRepository usuarios) {
        this.entregas = entregas;
        this.epis = epis;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<EntregaEpiResponse> listar() {
        return entregas.findAllByOrderByDataEntregaDescIdDesc().stream()
                .map(EntregaEpiResponse::from).toList();
    }

    @Transactional
    public EntregaEpiResponse criar(EntregaEpiRequest request) {
        return salvar(null, request);
    }

    @Transactional
    public EntregaEpiResponse atualizar(Long id, EntregaEpiRequest request) {
        return salvar(id, request);
    }

    @Transactional
    public void excluir(Long id) {
        EntregaEpi entrega = entregas.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega não encontrada."));
        restaurarEstoque(entrega);
        entregas.delete(entrega);
    }

    private EntregaEpiResponse salvar(Long id, EntregaEpiRequest request) {
        EntregaEpi entrega;
        if (id == null) {
            entrega = new EntregaEpi(usuarioAtivo(request.funcionarioId()),
                    request.dataEntrega(), request.observacoes());
        } else {
            entrega = entregas.findByIdForUpdate(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega não encontrada."));
            restaurarEstoque(entrega);
            entrega.getItens().clear();
            entrega.setUsuario(usuarioAtivo(request.funcionarioId()));
            entrega.setDataEntrega(request.dataEntrega());
            entrega.setObservacoes(request.observacoes());
        }

        Map<String, Integer> solicitados = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (EntregaEpiRequest.Item item : request.itens()) {
            String nome = item.nome().trim();
            if (solicitados.putIfAbsent(nome, item.quantidade()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cada EPI deve aparecer uma única vez na entrega.");
            }
        }

        for (var solicitado : solicitados.entrySet()) {
            int restante = solicitado.getValue();
            List<Epis> lotes = epis.findAllByNomeForUpdate(solicitado.getKey()).stream()
                    .filter(lote -> lote.getQuantidade() > 0 && !lote.getValidade().isBefore(LocalDate.now()))
                    .toList();
            int disponivel = lotes.stream().mapToInt(Epis::getQuantidade).sum();
            if (disponivel < restante) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Estoque insuficiente para " + solicitado.getKey() + ". Disponível: " + disponivel + ".");
            }
            for (Epis lote : lotes) {
                if (restante == 0) break;
                int retirada = Math.min(restante, lote.getQuantidade());
                lote.setQuantidade(lote.getQuantidade() - retirada);
                entrega.adicionarItem(new EntregaEpiItem(lote, retirada));
                restante -= retirada;
            }
        }
        return EntregaEpiResponse.from(entregas.save(entrega));
    }

    private void restaurarEstoque(EntregaEpi entrega) {
        for (EntregaEpiItem item : entrega.getItens()) {
            List<Epis> loteBloqueado = epis.findAllByNomeForUpdate(item.getEpi().getNome());
            Epis lote = loteBloqueado.stream()
                    .filter(candidato -> candidato.getId().equals(item.getEpi().getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "Não foi possível restaurar o estoque do EPI vinculado."));
            lote.setQuantidade(lote.getQuantidade() + item.getQuantidade());
        }
    }

    private Usuario usuarioAtivo(Long id) {
        Usuario usuario = usuarios.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));
        if (!"ATIVO".equalsIgnoreCase(usuario.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Somente usuários ativos podem receber EPI.");
        }
        return usuario;
    }
}
