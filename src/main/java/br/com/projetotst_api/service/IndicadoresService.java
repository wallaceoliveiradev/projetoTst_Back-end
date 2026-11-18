package br.com.projetotst_api.service;

import br.com.projetotst_api.dto.DashboardResponse;
import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;
import br.com.projetotst_api.repository.EpisRepository;
import br.com.projetotst_api.repository.HistoricoTreinamentoRepository;
import br.com.projetotst_api.repository.RiscoRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

@Service
public class IndicadoresService {

    private final Clock clock;
    private final HistoricoTreinamentoRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EpisRepository episRepository;
    private final RiscoRepository riscoRepository;

    public IndicadoresService(
            Clock clock,
            HistoricoTreinamentoRepository historicoRepository,
            UsuarioRepository usuarioRepository,
            EpisRepository episRepository,
            RiscoRepository riscoRepository) {
        this.clock = clock;
        this.historicoRepository = historicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.episRepository = episRepository;
        this.riscoRepository = riscoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse obterDashboard(int diasAlertaEpi) {
        if (diasAlertaEpi < 0 || diasAlertaEpi > 365) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "diasAlertaEpi deve estar entre 0 e 365.");
        }

        LocalDate hoje = LocalDate.now(clock);
        long treinamentosVencidos = historicoRepository.contarVencidos(hoje);
        long ciclosAtuais = historicoRepository.contarCiclosAtuais();
        long colaboradoresCertificados = historicoRepository.contarColaboradoresCertificados(hoje);
        long colaboradoresAtivos =
                usuarioRepository.countByPerfilIgnoreCaseAndStatusIgnoreCase("COLABORADOR", "ATIVO");
        long episProximos = episRepository.somarProximosDoVencimento(
                hoje, hoje.plusDays(diasAlertaEpi));
        long saldoEpis = episRepository.somarSaldoEstoque();
        long riscosAltos = riscoRepository.countByNivelAndSituacao(NivelRisco.ALTO, SituacaoRisco.ATIVO);
        long riscosAtivos = riscoRepository.countBySituacao(SituacaoRisco.ATIVO);

        return new DashboardResponse(
                hoje,
                Instant.now(clock),
                diasAlertaEpi,
                treinamentosVencidos,
                percentual(treinamentosVencidos, ciclosAtuais),
                episProximos,
                percentual(episProximos, saldoEpis),
                colaboradoresCertificados,
                percentual(colaboradoresCertificados, colaboradoresAtivos),
                riscosAltos,
                percentual(riscosAltos, riscosAtivos));
    }

    private double percentual(long parte, long total) {
        return total == 0 ? 0.0 : Math.round(parte * 1000.0 / total) / 10.0;
    }
}
