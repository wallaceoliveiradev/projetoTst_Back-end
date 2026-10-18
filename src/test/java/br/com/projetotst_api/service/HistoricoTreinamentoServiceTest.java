package br.com.projetotst_api.service;

import br.com.projetotst_api.entity.HistoricoTreinamento;
import br.com.projetotst_api.entity.ParametroTreinamento;
import br.com.projetotst_api.entity.StatusHistoricoTreinamento;
import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.HistoricoTreinamentoRepository;
import br.com.projetotst_api.repository.ParametroTreinamentoRepository;
import br.com.projetotst_api.repository.TreinamentoRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import br.com.projetotst_api.dto.HistoricoTreinamentoResponse;
import br.com.projetotst_api.dto.RegistroConclusaoTreinamentoRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

class HistoricoTreinamentoServiceTest {

    private HistoricoTreinamentoService service;
    private HistoricoTreinamentoRepository historicoRepository;
    private ParametroTreinamentoRepository parametroRepository;
    private TreinamentoRepository treinamentoRepository;
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void configurar() {
        historicoRepository = mock(HistoricoTreinamentoRepository.class);
        parametroRepository = mock(ParametroTreinamentoRepository.class);
        treinamentoRepository = mock(TreinamentoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        service = new HistoricoTreinamentoService(
                historicoRepository, parametroRepository, treinamentoRepository, usuarioRepository);
    }

    @Test
    void iniciaAlertaComSessentaDiasDeAntecedencia() {
        LocalDate hoje = LocalDate.of(2026, 8, 16);

        assertEquals("ALERTA", service.statusAtual(registro(hoje.plusDays(60)), hoje));
        assertEquals("VALIDO", service.statusAtual(registro(hoje.plusDays(61)), hoje));
    }

    @Test
    void venceSomenteDepoisDaDataLimite() {
        LocalDate hoje = LocalDate.of(2026, 10, 15);

        assertEquals("ALERTA", service.statusAtual(registro(hoje), hoje));
        assertEquals("VENCIDO", service.statusAtual(registro(hoje.minusDays(1)), hoje));
    }

    @Test
    void preservaStatusDeCiclosSubstituidosEInvalidados() {
        LocalDate hoje = LocalDate.of(2026, 8, 16);
        HistoricoTreinamento invalido = registro(hoje.plusDays(90));
        invalido.setStatus(StatusHistoricoTreinamento.INVALIDADO.name());
        HistoricoTreinamento substituido = registro(hoje.plusDays(90));
        substituido.setStatus(StatusHistoricoTreinamento.SUBSTITUIDO.name());

        assertEquals("INVALIDADO", service.statusAtual(invalido, hoje));
        assertEquals("SUBSTITUIDO", service.statusAtual(substituido, hoje));
    }

    @Test
    void registraCertificadoCalculandoVencimentoPelaRegraDoTreinamento() {
        Treinamento treinamento = mock(Treinamento.class);
        Usuario colaborador = mock(Usuario.class);
        when(treinamentoRepository.findById(1L)).thenReturn(Optional.of(treinamento));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(colaborador));
        when(treinamento.getId()).thenReturn(1L);
        when(treinamento.getNome()).thenReturn("Trabalho em Altura");
        when(treinamento.getNorma()).thenReturn("NR-35");
        when(treinamento.getValidade()).thenReturn(24);
        when(treinamento.getColaboradores()).thenReturn(new HashSet<>());
        when(colaborador.getId()).thenReturn(2L);
        when(colaborador.getNome()).thenReturn("Colaborador");
        when(colaborador.getEmail()).thenReturn("colaborador@example.com");
        when(colaborador.getCargo()).thenReturn("Operador");
        when(colaborador.getPerfil()).thenReturn("COLABORADOR");
        when(colaborador.getStatus()).thenReturn("ATIVO");
        when(parametroRepository.findByTreinamentoIdAndCargoIgnoreCase(1L, "Operador"))
                .thenReturn(Optional.empty());
        when(parametroRepository.findByTreinamentoIdAndCargoIsNull(1L)).thenReturn(Optional.empty());
        when(historicoRepository.findByTreinamentoIdAndColaboradorIdAndStatusNot(1L, 2L, "SUBSTITUIDO"))
                .thenReturn(List.of());
        when(treinamentoRepository.save(treinamento)).thenReturn(treinamento);
        when(historicoRepository.save(any(HistoricoTreinamento.class))).thenAnswer(invocation -> {
            HistoricoTreinamento registro = invocation.getArgument(0);
            registro.setId(9L);
            return registro;
        });

        HistoricoTreinamentoResponse resposta = service.registrar(
                new RegistroConclusaoTreinamentoRequest(
                        1L, 2L, LocalDate.of(2026, 10, 15), 8, "Instrutor", "Conteúdo programático"),
                new MockMultipartFile(
                        "certificado", "nr35.pdf", "application/pdf", "%PDF-1.7".getBytes()));

        assertEquals(LocalDate.of(2028, 10, 15), resposta.dataVencimento());
        assertEquals(24, resposta.validadeAplicadaMeses());
        assertEquals("VALIDO", resposta.status());
        assertEquals("application/pdf", resposta.certificadoContentType());
    }

    @Test
    void aplicaValidadeDaNr33DeAcordoComOCargo() {
        assertEquals(12, validadeRegistrada("Trabalhador"));
        assertEquals(12, validadeRegistrada("Vigia"));
        assertEquals(24, validadeRegistrada("Supervisor de espaço confinado"));
    }

    @Test
    void excluiSomenteARegraDoTreinamentoSolicitado() {
        var parametro = mock(ParametroTreinamento.class);
        when(parametroRepository.findByIdAndTreinamentoId(4L, 1L)).thenReturn(Optional.of(parametro));

        service.excluirParametro(1L, 4L);

        verify(parametroRepository).delete(parametro);
    }

    @Test
    void naoExcluiRegraDeOutroTreinamento() {
        when(parametroRepository.findByIdAndTreinamentoId(4L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.excluirParametro(1L, 4L));

        verify(parametroRepository, never()).delete(any(ParametroTreinamento.class));
    }

    private int validadeRegistrada(String cargo) {
        Treinamento treinamento = mock(Treinamento.class);
        Usuario colaborador = mock(Usuario.class);
        when(treinamentoRepository.findById(1L)).thenReturn(Optional.of(treinamento));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(colaborador));
        when(treinamento.getId()).thenReturn(1L);
        when(treinamento.getNome()).thenReturn("Espaço Confinado");
        when(treinamento.getNorma()).thenReturn("NR-33");
        when(treinamento.getValidade()).thenReturn(12);
        when(treinamento.getColaboradores()).thenReturn(new HashSet<>());
        when(colaborador.getId()).thenReturn(2L);
        when(colaborador.getNome()).thenReturn("Colaborador");
        when(colaborador.getEmail()).thenReturn("colaborador@example.com");
        when(colaborador.getCargo()).thenReturn(cargo);
        when(colaborador.getPerfil()).thenReturn("COLABORADOR");
        when(colaborador.getStatus()).thenReturn("ATIVO");
        when(parametroRepository.findByTreinamentoIdAndCargoIgnoreCase(1L, cargo))
                .thenReturn(Optional.empty());
        when(parametroRepository.findByTreinamentoIdAndCargoIsNull(1L)).thenReturn(Optional.empty());
        when(historicoRepository.findByTreinamentoIdAndColaboradorIdAndStatusNot(1L, 2L, "SUBSTITUIDO"))
                .thenReturn(List.of());
        when(treinamentoRepository.save(treinamento)).thenReturn(treinamento);
        when(historicoRepository.save(any(HistoricoTreinamento.class))).thenAnswer(invocation -> {
            HistoricoTreinamento registro = invocation.getArgument(0);
            registro.setId(9L);
            return registro;
        });

        return service.registrar(
                new RegistroConclusaoTreinamentoRequest(
                        1L, 2L, LocalDate.of(2026, 10, 15), 8, "Instrutor", "Conteúdo programático"),
                new MockMultipartFile(
                        "certificado", "nr33.pdf", "application/pdf", "%PDF-1.7".getBytes()))
                .validadeAplicadaMeses();
    }

    private HistoricoTreinamento registro(LocalDate vencimento) {
        Treinamento treinamento = new Treinamento("Treinamento", "NR-35", "Descrição", 8, 24, 1, "ATIVO");
        Usuario colaborador = new Usuario("Colaborador", "colaborador@example.com", "senha", "COLABORADOR");
        return new HistoricoTreinamento(
                treinamento,
                colaborador,
                vencimento.minusMonths(24),
                vencimento,
                24,
                60,
                8,
                "Instrutor",
                "Conteúdo",
                "certificado.pdf",
                "application/pdf",
                "%PDF-1.7".getBytes());
    }
}
