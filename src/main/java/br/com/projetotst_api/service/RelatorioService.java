package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.RelatorioDadosResponse;
import br.com.projetotst_api.dto.RelatorioResumoResponse;
import br.com.projetotst_api.dto.RelatorioResumoResponse.Quantidade;
import br.com.projetotst_api.dto.RelatorioResumoResponse.QuantidadeMensal;
import br.com.projetotst_api.entity.StatusHistoricoTreinamento;
import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;
import br.com.projetotst_api.repository.EntregaEpiRepository;
import br.com.projetotst_api.repository.HistoricoTreinamentoRepository;
import br.com.projetotst_api.repository.InspecaoRepository;
import br.com.projetotst_api.repository.RiscoRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    private static final DateTimeFormatter ANO_MES = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final List<String> STATUS_CHECKLIST =
            List.of("conforme", "naoConforme", "pendente");

    private final Clock clock;
    private final HistoricoTreinamentoRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntregaEpiRepository entregaRepository;
    private final RiscoRepository riscoRepository;
    private final InspecaoRepository inspecaoRepository;

    public RelatorioService(
            Clock clock,
            HistoricoTreinamentoRepository historicoRepository,
            UsuarioRepository usuarioRepository,
            EntregaEpiRepository entregaRepository,
            RiscoRepository riscoRepository,
            InspecaoRepository inspecaoRepository) {
        this.clock = clock;
        this.historicoRepository = historicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.entregaRepository = entregaRepository;
        this.riscoRepository = riscoRepository;
        this.inspecaoRepository = inspecaoRepository;
    }

    @Transactional(readOnly = true)
    public RelatorioResumoResponse resumo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        LocalDate hoje = LocalDate.now(clock);
        return new RelatorioResumoResponse(
                inicio,
                fim,
                hoje,
                Instant.now(clock),
                usuarioRepository.countByPerfilIgnoreCaseAndStatusIgnoreCase("COLABORADOR", "ATIVO"),
                historicoRepository.countByDataConclusaoBetween(inicio, fim),
                entregaRepository.somarUnidadesEntregues(inicio, fim),
                riscoRepository.countBySituacao(SituacaoRisco.ATIVO),
                historicoRepository.contarConclusoesPorTreinamento(inicio, fim).stream()
                        .map(item -> new Quantidade(item.getRotulo(), item.getQuantidade()))
                        .toList(),
                entregaRepository.somarUnidadesPorEpi(inicio, fim).stream()
                        .map(item -> new Quantidade(item.getRotulo(), item.getQuantidade()))
                        .toList(),
                contagensRiscosAtivos(),
                contagensMensais(inicio, fim));
    }

    @Transactional(readOnly = true)
    public RelatorioDadosResponse dados(String tipo, LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        if (tipo == null || !List.of(
                "geral", "funcionarios", "treinamentos", "epis", "riscos", "inspecoes", "checklist")
                .contains(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de relatório inválido.");
        }

        Instant geradoEm = Instant.now(clock);
        List<String> colunas;
        List<List<Object>> linhas;
        String titulo;

        switch (tipo) {
            case "geral" -> {
                RelatorioResumoResponse resumo = resumo(inicio, fim);
                titulo = "Relatório Geral";
                colunas = List.of("Indicador", "Quantidade", "Referência");
                linhas = new ArrayList<>();
                linhas.add(linha("Colaboradores ativos", resumo.colaboradoresAtivosAtuais(), "Situação atual"));
                linhas.add(linha("Conclusões registradas", resumo.conclusoesNoPeriodo(), "Período"));
                linhas.add(linha("Unidades de EPI entregues", resumo.unidadesEpiEntreguesNoPeriodo(), "Período"));
                linhas.add(linha("Riscos ativos", resumo.riscosAtivosAtuais(), "Situação atual"));
            }
            case "funcionarios" -> {
                titulo = "Colaboradores por situação atual";
                colunas = List.of("Situação atual", "Quantidade");
                linhas = List.of(
                        linha("Ativos", usuarioRepository.countByPerfilIgnoreCaseAndStatusIgnoreCase(
                                "COLABORADOR", "ATIVO")),
                        linha("Inativos", usuarioRepository.countByPerfilIgnoreCaseAndStatusIgnoreCase(
                                "COLABORADOR", "INATIVO")));
            }
            case "treinamentos" -> {
                titulo = "Conclusões por treinamento";
                colunas = List.of("Treinamento", "Conclusões registradas", "Invalidadas");
                Map<String, long[]> contagens = new java.util.LinkedHashMap<>();
                for (var contagem : historicoRepository.contarConclusoesPorTreinamentoEStatus(inicio, fim)) {
                    long[] totais = contagens.computeIfAbsent(contagem.getRotulo(), ignorado -> new long[2]);
                    totais[0] += contagem.getQuantidade();
                    if (StatusHistoricoTreinamento.INVALIDADO.name().equals(contagem.getStatus())) {
                        totais[1] += contagem.getQuantidade();
                    }
                }
                linhas = new ArrayList<>();
                contagens.forEach((rotulo, totais) -> linhas.add(linha(rotulo, totais[0], totais[1])));
            }
            case "epis" -> {
                titulo = "Unidades de EPI entregues por tipo";
                colunas = List.of("EPI", "Unidades entregues");
                linhas = entregaRepository.somarUnidadesPorEpi(inicio, fim).stream()
                        .map(item -> linha(item.getRotulo(), item.getQuantidade()))
                        .toList();
            }
            case "riscos" -> {
                titulo = "Riscos ativos por nível (situação atual)";
                colunas = List.of("Nível", "Riscos ativos");
                Map<NivelRisco, Long> contagens = new EnumMap<>(NivelRisco.class);
                riscoRepository.contarPorNivelESituacao(SituacaoRisco.ATIVO)
                        .forEach(item -> contagens.put(item.getRotulo(), item.getQuantidade()));
                linhas = new ArrayList<>();
                for (NivelRisco nivel : NivelRisco.values()) {
                    linhas.add(linha(rotuloNivel(nivel), contagens.getOrDefault(nivel, 0L)));
                }
            }
            case "inspecoes" -> {
                titulo = "Inspeções e respostas do checklist";
                colunas = List.of("Indicador", "Quantidade");
                Map<String, Long> respostas = contagensChecklist(inicio, fim);
                linhas = new ArrayList<>();
                linhas.add(linha("Inspeções realizadas", inspecaoRepository.countByDataBetween(inicio, fim)));
                linhas.add(linha("Respostas de checklist", respostas.values().stream().mapToLong(Long::longValue).sum()));
                for (String status : STATUS_CHECKLIST) {
                    linhas.add(linha(rotuloStatus(status), respostas.getOrDefault(status, 0L)));
                }
            }
            case "checklist" -> {
                titulo = "Respostas do checklist por situação";
                colunas = List.of("Situação", "Quantidade");
                Map<String, Long> respostas = contagensChecklist(inicio, fim);
                linhas = STATUS_CHECKLIST.stream()
                        .map(status -> linha(rotuloStatus(status), respostas.getOrDefault(status, 0L)))
                        .toList();
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de relatório inválido.");
        }

        return new RelatorioDadosResponse(tipo, titulo, inicio, fim, geradoEm, colunas, linhas);
    }

    private List<Quantidade> contagensRiscosAtivos() {
        Map<NivelRisco, Long> contagens = new EnumMap<>(NivelRisco.class);
        riscoRepository.contarPorNivelESituacao(SituacaoRisco.ATIVO)
                .forEach(item -> contagens.put(item.getRotulo(), item.getQuantidade()));
        return List.of(NivelRisco.ALTO, NivelRisco.MEDIO, NivelRisco.BAIXO).stream()
                .map(nivel -> new Quantidade(rotuloNivel(nivel), contagens.getOrDefault(nivel, 0L)))
                .toList();
    }

    private List<QuantidadeMensal> contagensMensais(LocalDate inicio, LocalDate fim) {
        Map<YearMonth, Long> contagens = new HashMap<>();
        historicoRepository.contarConclusoesPorMes(inicio, fim).forEach(item ->
                contagens.put(YearMonth.of(item.getAno(), item.getMes()), item.getQuantidade()));

        List<QuantidadeMensal> meses = new ArrayList<>();
        YearMonth atual = YearMonth.from(inicio);
        YearMonth ultimo = YearMonth.from(fim);
        while (!atual.isAfter(ultimo)) {
            meses.add(new QuantidadeMensal(
                    ANO_MES.format(atual.atDay(1)), contagens.getOrDefault(atual, 0L)));
            atual = atual.plusMonths(1);
        }
        return meses;
    }

    private Map<String, Long> contagensChecklist(LocalDate inicio, LocalDate fim) {
        Map<String, Long> contagens = new HashMap<>();
        inspecaoRepository.contarRespostasPorStatus(inicio, fim)
                .forEach(item -> contagens.put(item.getRotulo(), item.getQuantidade()));
        return contagens;
    }

    private String rotuloNivel(NivelRisco nivel) {
        return switch (nivel) {
            case ALTO -> "Alto";
            case MEDIO -> "Médio";
            case BAIXO -> "Baixo";
        };
    }

    private String rotuloStatus(String status) {
        return switch (status) {
            case "conforme" -> "Conformes";
            case "naoConforme" -> "Não conformes";
            case "pendente" -> "Pendentes";
            default -> status;
        };
    }

    private List<Object> linha(Object... valores) {
        return java.util.Arrays.asList(valores);
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe as datas inicial e final.");
        }
        if (inicio.isAfter(fim)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A data inicial não pode ser posterior à data final.");
        }
    }
}
