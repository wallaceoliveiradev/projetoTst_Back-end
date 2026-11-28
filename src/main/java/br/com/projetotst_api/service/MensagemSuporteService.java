package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.MensagemSuporteRequest;
import br.com.projetotst_api.dto.MensagemSuporteResponse;
import br.com.projetotst_api.entity.MensagemSuporte;
import br.com.projetotst_api.repository.MensagemSuporteRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MensagemSuporteService {

    private final MensagemSuporteRepository mensagens;
    private final UsuarioRepository usuarios;

    public MensagemSuporteService(MensagemSuporteRepository mensagens, UsuarioRepository usuarios) {
        this.mensagens = mensagens;
        this.usuarios = usuarios;
    }

    @Transactional
    public MensagemSuporteResponse criar(String email, MensagemSuporteRequest request) {
        var usuario = usuarios.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        var mensagem = mensagens.save(new MensagemSuporte(
                usuario,
                request.tipo(),
                request.mensagem().trim()));
        return MensagemSuporteResponse.from(mensagem);
    }

    @Transactional(readOnly = true)
    public List<MensagemSuporteResponse> listarParaAdmin() {
        return mensagens.findAllByOrderByCriadaEmDesc().stream()
                .map(MensagemSuporteResponse::from)
                .toList();
    }

    @Transactional
    public MensagemSuporteResponse marcarComoVisualizada(Long id) {
        var mensagem = obter(id);
        mensagem.setVisualizada(true);
        return MensagemSuporteResponse.from(mensagens.save(mensagem));
    }

    @Transactional
    public void excluir(Long id) {
        mensagens.delete(obter(id));
    }

    private MensagemSuporte obter(Long id) {
        return mensagens.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Mensagem de suporte não encontrada."));
    }
}
