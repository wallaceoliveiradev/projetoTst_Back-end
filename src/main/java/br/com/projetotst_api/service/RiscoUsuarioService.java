package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.RiscoUsuarioRequest;
import br.com.projetotst_api.dto.RiscoUsuarioResponse;
import br.com.projetotst_api.entity.Risco;
import br.com.projetotst_api.entity.RiscoUsuario;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.RiscoRepository;
import br.com.projetotst_api.repository.RiscoUsuarioRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RiscoUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RiscoRepository riscoRepository;
    private final RiscoUsuarioRepository associacaoRepository;

    public RiscoUsuarioService(
            UsuarioRepository usuarioRepository,
            RiscoRepository riscoRepository,
            RiscoUsuarioRepository associacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.riscoRepository = riscoRepository;
        this.associacaoRepository = associacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<RiscoUsuarioResponse> listarPorUsuarioId(Long usuarioId) {
        buscarUsuario(usuarioId);
        return associacaoRepository.findByUsuario_IdOrderByIdAsc(usuarioId)
                .stream()
                .map(RiscoUsuarioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RiscoUsuarioResponse> listarDoUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        return associacaoRepository.findByUsuario_IdOrderByIdAsc(usuario.getId())
                .stream()
                .map(RiscoUsuarioResponse::from)
                .toList();
    }

    @Transactional
    public List<RiscoUsuarioResponse> substituirAssociacoes(Long usuarioId, List<RiscoUsuarioRequest> requests) {
        Usuario usuario = buscarUsuario(usuarioId);
        Set<Long> riscoIds = requests.stream()
                .map(RiscoUsuarioRequest::riscoId)
                .collect(Collectors.toSet());
        if (riscoIds.size() != requests.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido associar o mesmo risco mais de uma vez.");
        }

        Map<Long, Risco> riscos = riscoRepository.findAllById(riscoIds).stream()
                .collect(Collectors.toMap(Risco::getId, Function.identity()));
        if (riscos.size() != riscoIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uma ou mais referências de risco não existem.");
        }

        associacaoRepository.deleteAll(
                associacaoRepository.findByUsuario_IdOrderByIdAsc(usuarioId));
        associacaoRepository.flush();

        List<RiscoUsuario> novasAssociacoes = requests.stream()
                .map(request -> new RiscoUsuario(
                        usuario,
                        riscos.get(request.riscoId()),
                        request.validade(),
                        request.validado()))
                .toList();
        associacaoRepository.saveAll(novasAssociacoes);
        return associacaoRepository.findByUsuario_IdOrderByIdAsc(usuarioId)
                .stream()
                .map(RiscoUsuarioResponse::from)
                .toList();
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }
}
