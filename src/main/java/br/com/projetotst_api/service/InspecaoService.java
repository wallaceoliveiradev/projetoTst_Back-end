package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.InspecaoRequest;
import br.com.projetotst_api.dto.InspecaoResponse;
import br.com.projetotst_api.entity.Inspecao;
import br.com.projetotst_api.entity.RespostaInspecao;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.InspecaoRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InspecaoService {

    private final InspecaoRepository inspecoes;
    private final UsuarioRepository usuarios;

    public InspecaoService(InspecaoRepository inspecoes, UsuarioRepository usuarios) {
        this.inspecoes = inspecoes;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<InspecaoResponse> listar(String email) {
        Usuario usuario = usuarioAutenticado(email);
        List<Inspecao> resultado = "ADMIN".equalsIgnoreCase(usuario.getPerfil())
                ? inspecoes.findAllByOrderByDataDescIdDesc()
                : inspecoes.findAllByUsuarioIdOrderByDataDescIdDesc(usuario.getId());
        return resultado.stream().map(InspecaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public InspecaoResponse obter(Long id, String email) {
        Usuario usuario = usuarioAutenticado(email);
        Inspecao inspecao = inspecoes.findDetalhadaById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspeção não encontrada."));
        if (!"ADMIN".equalsIgnoreCase(usuario.getPerfil())
                && !usuario.getId().equals(inspecao.getUsuario().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspeção não encontrada.");
        }
        return InspecaoResponse.from(inspecao);
    }

    @Transactional
    public InspecaoResponse criar(InspecaoRequest request, String email) {
        Usuario usuario = usuarioAutenticado(email);
        validarRespostas(request.respostas());
        Inspecao inspecao = new Inspecao(usuario, request.data(), request.tipoId(),
                request.tipo().trim(), limpar(request.local()), limpar(request.setor()),
                limpar(request.numeroOnibus()), limpar(request.placa()), request.responsavel().trim(),
                limpar(request.observacoesGerais()));
        adicionarRespostas(inspecao, request);
        return InspecaoResponse.from(inspecoes.save(inspecao));
    }

    @Transactional
    public InspecaoResponse atualizar(Long id, InspecaoRequest request, String email) {
        Usuario usuario = usuarioAutenticado(email);
        Inspecao inspecao = encontrarPorUsuario(id, usuario);
        validarRespostas(request.respostas());
        inspecao.atualizar(request.data(), request.tipoId(), request.tipo().trim(),
                limpar(request.local()), limpar(request.setor()), limpar(request.numeroOnibus()),
                limpar(request.placa()), request.responsavel().trim(), limpar(request.observacoesGerais()));
        adicionarRespostas(inspecao, request);
        return InspecaoResponse.from(inspecoes.save(inspecao));
    }

    private Usuario usuarioAutenticado(String email) {
        return usuarios.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado."));
    }

    private Inspecao encontrarPorUsuario(Long id, Usuario usuario) {
        if ("ADMIN".equalsIgnoreCase(usuario.getPerfil())) {
            return inspecoes.findDetalhadaById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Inspeção não encontrada."));
        }
        return inspecoes.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Inspeção não encontrada."));
    }

    private void adicionarRespostas(Inspecao inspecao, InspecaoRequest request) {
        request.respostas().forEach(resposta -> inspecao.adicionarResposta(new RespostaInspecao(
                resposta.itemId().trim(), resposta.descricao().trim(), resposta.status(),
                limpar(resposta.observacao()), resposta.prioridade(), resposta.prazoCorrecao(),
                limpar(resposta.responsavelCorrecao()))));
    }

    private void validarRespostas(List<InspecaoRequest.Resposta> respostas) {
        Set<String> ids = new HashSet<>();
        for (InspecaoRequest.Resposta resposta : respostas) {
            if (!ids.add(resposta.itemId().trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cada item do checklist deve ter uma única resposta.");
            }
            if ("naoConforme".equals(resposta.status()) && (resposta.observacao() == null
                    || resposta.observacao().isBlank())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Descreva o problema de cada item não conforme.");
            }
        }
    }

    private String limpar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
