package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.HistoricoTreinamentoResponse;
import br.com.projetotst_api.dto.InvalidacaoTreinamentoRequest;
import br.com.projetotst_api.dto.ParametroTreinamentoRequest;
import br.com.projetotst_api.dto.ParametroTreinamentoResponse;
import br.com.projetotst_api.dto.RegistroConclusaoTreinamentoRequest;
import br.com.projetotst_api.entity.HistoricoTreinamento;
import br.com.projetotst_api.entity.ParametroTreinamento;
import br.com.projetotst_api.entity.StatusHistoricoTreinamento;
import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.TreinamentoRepository;
import br.com.projetotst_api.repository.HistoricoTreinamentoRepository;
import br.com.projetotst_api.repository.ParametroTreinamentoRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class HistoricoTreinamentoService {

    private static final long TAMANHO_MAXIMO_CERTIFICADO = 10L * 1024 * 1024;
    private static final int ANTECEDENCIA_PADRAO_DIAS = 60;

    private final HistoricoTreinamentoRepository historicoRepository;
    private final ParametroTreinamentoRepository parametroRepository;
    private final TreinamentoRepository treinamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoTreinamentoService(HistoricoTreinamentoRepository historicoRepository,
                                      ParametroTreinamentoRepository parametroRepository,
                                      TreinamentoRepository treinamentoRepository,
                                      UsuarioRepository usuarioRepository) {
        this.historicoRepository = historicoRepository;
        this.parametroRepository = parametroRepository;
        this.treinamentoRepository = treinamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<HistoricoTreinamentoResponse> listar(String email, boolean administrador) {
        List<HistoricoTreinamento> registros = administrador
                ? historicoRepository.findAllByOrderByDataConclusaoDesc()
                : historicoRepository.findDistinctByColaboradorEmailIgnoreCaseOrderByDataConclusaoDesc(email);
        LocalDate hoje = LocalDate.now();
        return registros.stream()
                .map(registro -> HistoricoTreinamentoResponse.from(registro, statusAtual(registro, hoje)))
                .toList();
    }

    @Transactional
    public HistoricoTreinamentoResponse registrar(RegistroConclusaoTreinamentoRequest request,
                                                  MultipartFile arquivo) {
        validarArquivo(arquivo);
        if (request.dataConclusao().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de conclusão não pode ser futura.");
        }

        Treinamento treinamento = treinamentoRepository.findById(request.treinamentoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treinamento não encontrado."));
        Usuario colaborador = usuarioRepository.findById(request.usuarioId())
                .filter(usuario -> "COLABORADOR".equalsIgnoreCase(usuario.getPerfil()))
                .filter(usuario -> "ATIVO".equalsIgnoreCase(usuario.getStatus()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Selecione um colaborador ativo."));

        RegraValidade regra = obterRegra(treinamento, colaborador);
        byte[] conteudo;
        try {
            conteudo = arquivo.getBytes();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler o certificado.", exception);
        }
        String contentType = detectarTipo(conteudo);

        List<HistoricoTreinamento> ciclosAnteriores =
                historicoRepository.findByTreinamentoIdAndColaboradorIdAndStatusNot(
                        treinamento.getId(), colaborador.getId(), StatusHistoricoTreinamento.SUBSTITUIDO.name());
        ciclosAnteriores.forEach(
                registro -> registro.setStatus(StatusHistoricoTreinamento.SUBSTITUIDO.name()));

        HistoricoTreinamento registro = new HistoricoTreinamento(
                treinamento,
                colaborador,
                request.dataConclusao(),
                request.dataConclusao().plusMonths(regra.validadeMeses()),
                regra.validadeMeses(),
                regra.diasAntecedenciaAlerta(),
                request.cargaHoraria(),
                request.instrutor().trim(),
                request.conteudoProgramatico().trim(),
                nomeSeguro(arquivo.getOriginalFilename(), contentType),
                contentType,
                conteudo);
        treinamento.getColaboradores().add(colaborador);
        treinamentoRepository.save(treinamento);
        HistoricoTreinamento salvo = historicoRepository.save(registro);
        return HistoricoTreinamentoResponse.from(salvo, statusAtual(salvo, LocalDate.now()));
    }

    @Transactional
    public HistoricoTreinamento invalidar(long id, InvalidacaoTreinamentoRequest request) {
        HistoricoTreinamento registro = historicoRepository.findWithTreinamentoAndColaboradorById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificado não encontrado."));
        if (StatusHistoricoTreinamento.SUBSTITUIDO.name().equals(registro.getStatus())
                || StatusHistoricoTreinamento.INVALIDADO.name().equals(registro.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este ciclo não pode ser invalidado novamente.");
        }
        registro.invalidar(request.motivo().trim(), LocalDate.now());
        return registro;
    }

    @Transactional(readOnly = true)
    public HistoricoTreinamento obter(long id, String email, boolean administrador) {
        HistoricoTreinamento registro = historicoRepository.findWithTreinamentoAndColaboradorById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificado não encontrado."));
        if (!administrador && !registro.getColaborador().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificado não encontrado.");
        }
        return registro;
    }

    @Transactional(readOnly = true)
    public List<ParametroTreinamentoResponse> listarParametros() {
        return parametroRepository.findAllByOrderByTreinamentoNomeAscCargoAsc().stream()
                .map(ParametroTreinamentoResponse::from)
                .toList();
    }

    @Transactional
    public ParametroTreinamentoResponse salvarParametro(Long treinamentoId, Long parametroId,
                                                        ParametroTreinamentoRequest request) {
        validarParametro(request);
        Treinamento treinamento = treinamentoRepository.findById(treinamentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treinamento não encontrado."));
        String cargo = limparCargo(request.cargo());

        var regraExistente = cargo == null
                ? parametroRepository.findByTreinamentoIdAndCargoIsNull(treinamentoId)
                : parametroRepository.findByTreinamentoIdAndCargoIgnoreCase(treinamentoId, cargo);
        ParametroTreinamento parametro = parametroId == null
                ? regraExistente.orElseGet(() -> new ParametroTreinamento(
                        treinamento, cargo, request.validadeMeses(), request.diasAntecedenciaAlerta()))
                : parametroRepository.findById(parametroId)
                        .filter(atual -> atual.getTreinamento().getId().equals(treinamentoId))
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Parâmetro não encontrado."));

        if (parametroId != null && regraExistente.isPresent()
                && !regraExistente.get().getId().equals(parametro.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma regra para esse cargo.");
        }

        parametro.setCargo(cargo);
        parametro.setValidadeMeses(request.validadeMeses());
        parametro.setDiasAntecedenciaAlerta(request.diasAntecedenciaAlerta());
        ParametroTreinamento salvo = parametroRepository.save(parametro);
        return ParametroTreinamentoResponse.from(salvo);
    }

    @Transactional
    public void excluirParametro(Long treinamentoId, Long parametroId) {
        ParametroTreinamento parametro = parametroRepository.findByIdAndTreinamentoId(parametroId, treinamentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parâmetro não encontrado."));
        parametroRepository.delete(parametro);
    }

    private RegraValidade obterRegra(Treinamento treinamento, Usuario colaborador) {
        if (colaborador.getCargo() != null && !colaborador.getCargo().isBlank()) {
            var especifica = parametroRepository.findByTreinamentoIdAndCargoIgnoreCase(
                    treinamento.getId(), colaborador.getCargo().trim());
            if (especifica.isPresent()) {
                ParametroTreinamento regra = especifica.get();
                return new RegraValidade(regra.getValidadeMeses(), regra.getDiasAntecedenciaAlerta());
            }
        }

        var geral = parametroRepository.findByTreinamentoIdAndCargoIsNull(treinamento.getId());
        if (geral.isPresent()) {
            ParametroTreinamento regra = geral.get();
            return new RegraValidade(regra.getValidadeMeses(), regra.getDiasAntecedenciaAlerta());
        }

        if (normalizarNorma(treinamento.getNorma()).equals("NR33")) {
            boolean supervisor = colaborador.getCargo() != null
                    && colaborador.getCargo().toLowerCase(Locale.ROOT).contains("supervisor");
            return new RegraValidade(supervisor ? 24 : 12, ANTECEDENCIA_PADRAO_DIAS);
        }

        return new RegraValidade(treinamento.getValidade(), ANTECEDENCIA_PADRAO_DIAS);
    }

    String statusAtual(HistoricoTreinamento registro, LocalDate hoje) {
        if (!StatusHistoricoTreinamento.VALIDO.name().equals(registro.getStatus())) {
            return registro.getStatus();
        }
        if (registro.getDataVencimento().isBefore(hoje)) {
            return StatusHistoricoTreinamento.VENCIDO.name();
        }
        if (!hoje.plusDays(registro.getDiasAntecedenciaAlerta()).isBefore(registro.getDataVencimento())) {
            return StatusHistoricoTreinamento.ALERTA.name();
        }
        return StatusHistoricoTreinamento.VALIDO.name();
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anexe o certificado em PDF ou imagem.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO_CERTIFICADO) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "O certificado deve ter até 10 MB.");
        }
        try {
            detectarTipo(arquivo.getBytes());
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível validar o certificado.", exception);
        }
    }

    private String detectarTipo(byte[] arquivo) {
        if (comecaCom(arquivo, "%PDF-".getBytes())) return "application/pdf";
        if (comecaCom(arquivo, new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}))
            return "image/png";
        if (comecaCom(arquivo, new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF})) return "image/jpeg";
        if (comecaCom(arquivo, "GIF87a".getBytes()) || comecaCom(arquivo, "GIF89a".getBytes()))
            return "image/gif";
        if (comecaCom(arquivo, "BM".getBytes())) return "image/bmp";
        if (arquivo.length >= 12 && new String(arquivo, 0, 4).equals("RIFF")
                && new String(arquivo, 8, 4).equals("WEBP")) return "image/webp";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo deve ser um PDF ou imagem válida.");
    }

    private boolean comecaCom(byte[] valor, byte[] prefixo) {
        if (valor.length < prefixo.length) return false;
        for (int indice = 0; indice < prefixo.length; indice++) {
            if (valor[indice] != prefixo[indice]) return false;
        }
        return true;
    }

    private void validarParametro(ParametroTreinamentoRequest request) {
        if (request == null || request.validadeMeses() == null || request.validadeMeses() < 1
                || request.validadeMeses() > 120 || request.diasAntecedenciaAlerta() == null
                || request.diasAntecedenciaAlerta() < 0 || request.diasAntecedenciaAlerta() > 365
                || (request.cargo() != null && request.cargo().length() > 100)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe cargo, validade (1–120 meses) e antecedência (0–365 dias) válidos.");
        }
    }

    private String limparCargo(String cargo) {
        return cargo == null || cargo.isBlank() ? null : cargo.trim();
    }

    private String nomeSeguro(String nome, String contentType) {
        String nomeBase = nome == null || nome.isBlank() ? "certificado" : nome.replace('\\', '/');
        nomeBase = nomeBase.substring(nomeBase.lastIndexOf('/') + 1)
                .replaceAll("[\\p{Cntrl}<>:\"|?*]", "_")
                .trim();
        if (nomeBase.isBlank()) {
            nomeBase = "certificado";
        }
        String extensao = switch (contentType) {
            case "application/pdf" -> ".pdf";
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/gif" -> ".gif";
            case "image/bmp" -> ".bmp";
            case "image/webp" -> ".webp";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de certificado inválido.");
        };
        int ponto = nomeBase.lastIndexOf('.');
        if (ponto > 0) {
            nomeBase = nomeBase.substring(0, ponto);
        }
        int maximoBase = 255 - extensao.length();
        return nomeBase.substring(0, Math.min(nomeBase.length(), maximoBase)) + extensao;
    }

    private String normalizarNorma(String norma) {
        return norma == null ? "" : norma.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }

    private record RegraValidade(int validadeMeses, int diasAntecedenciaAlerta) {
    }
}
