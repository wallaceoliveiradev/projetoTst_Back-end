package br.com.projetotst_api;

import br.com.projetotst_api.entity.Risco;
import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.entity.Epis;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.EpisRepository;
import br.com.projetotst_api.repository.InspecaoRepository;
import br.com.projetotst_api.repository.RiscoRepository;
import br.com.projetotst_api.repository.TreinamentoRepository;
import br.com.projetotst_api.repository.UsuarioRepository;
import br.com.projetotst_api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TstApiApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private org.springframework.security.web.FilterChainProxy filterChain;

	@Autowired
	private TreinamentoRepository treinamentoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private RiscoRepository riscoRepository;

	@Autowired
	private EpisRepository episRepository;

	@Autowired
	private InspecaoRepository inspecaoRepository;

	@BeforeEach
	void configurarMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilters(filterChain)
				.build();
	}

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void administradorEntregaVariosEpisEFuncionarioPodeReceberNovamente() throws Exception {
		Usuario administrador = criarUsuario("ADMIN");
		Usuario colaborador = criarColaborador();
		var validade = java.time.LocalDate.now().plusYears(1);
		Epis capacete = episRepository.saveAndFlush(new Epis(
				"Capacete teste", "Cabeça", java.time.LocalDate.now(), 5, validade, "CA 1"));
		Epis oculos = episRepository.saveAndFlush(new Epis(
				"Óculos teste", "Olhos", java.time.LocalDate.now(), 2, validade, "CA 2"));
		String token = "Bearer " + token(administrador.getEmail(), "ADMIN");
		String primeiraEntrega = """
				{
				  "funcionarioId": %d,
				  "dataEntrega": "%s",
				  "observacoes": "",
				  "itens": [
				    { "nome": "Capacete teste", "quantidade": 2 },
				    { "nome": "Óculos teste", "quantidade": 1 }
				  ]
				}
				""".formatted(colaborador.getId(), java.time.LocalDate.now());

		mockMvc.perform(post("/api/epis/entregas")
						.header("Authorization", token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(primeiraEntrega))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.funcionarioId").value(colaborador.getId().intValue()))
				.andExpect(jsonPath("$.itens.length()").value(2));

		String segundaEntrega = """
				{
				  "funcionarioId": %d,
				  "dataEntrega": "%s",
				  "observacoes": "",
				  "itens": [
				    { "nome": "Capacete teste", "quantidade": 1 }
				  ]
				}
				""".formatted(colaborador.getId(), java.time.LocalDate.now());
		mockMvc.perform(post("/api/epis/entregas")
						.header("Authorization", token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(segundaEntrega))
				.andExpect(status().isCreated());

		org.junit.jupiter.api.Assertions.assertEquals(2,
				episRepository.findById(capacete.getId()).orElseThrow().getQuantidade());
		org.junit.jupiter.api.Assertions.assertEquals(1,
				episRepository.findById(oculos.getId()).orElseThrow().getQuantidade());
	}

	@Test
	void consultaSemTokenRetornaNaoAutorizado() throws Exception {
		mockMvc.perform(get("/api/treinamentos"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@Transactional
	void indicadoresERelatoriosExigemAdministradorERespondemComDadosAgregados() throws Exception {
		Usuario administrador = criarUsuario("ADMIN");
		Usuario colaborador = criarColaborador();
		String periodo = "?inicio=2026-10-01&fim=2026-10-31";

		mockMvc.perform(get("/api/dashboard"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/dashboard")
						.header("Authorization", "Bearer " + token(colaborador.getEmail(), "COLABORADOR")))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/api/relatorios/resumo" + periodo)
						.header("Authorization", "Bearer " + token(colaborador.getEmail(), "COLABORADOR")))
				.andExpect(status().isForbidden());

		String authorization = "Bearer " + token(administrador.getEmail(), "ADMIN");
		mockMvc.perform(get("/api/dashboard").header("Authorization", authorization))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dataReferencia").exists())
				.andExpect(jsonPath("$.treinamentosVencidos").isNumber())
				.andExpect(jsonPath("$.percentualEpisProximosVencimento").isNumber());
		mockMvc.perform(get("/api/dashboard")
						.param("diasAlertaEpi", "366")
						.header("Authorization", authorization))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/api/relatorios/resumo" + periodo)
						.header("Authorization", authorization))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.inicio").value("2026-10-01"))
				.andExpect(jsonPath("$.fim").value("2026-10-31"))
				.andExpect(jsonPath("$.treinamentosPorMes").isArray())
				.andExpect(jsonPath("$.riscosPorNivel").isArray());

		for (String tipo : new String[]{
				"geral", "funcionarios", "treinamentos", "epis", "riscos", "inspecoes", "checklist"}) {
			mockMvc.perform(get("/api/relatorios/dados")
							.param("tipo", tipo)
							.param("inicio", "2026-10-01")
							.param("fim", "2026-10-31")
							.header("Authorization", authorization))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.tipo").value(tipo))
					.andExpect(jsonPath("$.colunas").isArray())
					.andExpect(jsonPath("$.linhas").isArray());
		}

		mockMvc.perform(get("/api/relatorios/dados")
						.param("tipo", "desconhecido")
						.param("inicio", "2026-10-01")
						.param("fim", "2026-10-31")
						.header("Authorization", authorization))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/relatorios/resumo")
						.param("inicio", "2026-10-31")
						.param("fim", "2026-10-01")
						.header("Authorization", authorization))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	void colaboradorRecebeSomenteTreinamentosInscritos() throws Exception {
		Usuario colaborador = criarColaborador();
		Treinamento inscrito = treinamento("Treinamento inscrito");
		inscrito.getColaboradores().add(colaborador);
		inscrito = treinamentoRepository.saveAndFlush(inscrito);
		treinamentoRepository.saveAndFlush(treinamento("Treinamento nao inscrito"));

		mockMvc.perform(get("/api/treinamentos")
						.header("Authorization", "Bearer " + token(colaborador.getEmail(), "COLABORADOR")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(inscrito.getId().intValue()));
	}

	@Test
	@Transactional
	void administradorVeTodosOsTreinamentos() throws Exception {
		Usuario administrador = criarUsuario("ADMIN");
		Usuario colaborador = criarColaborador();
		Treinamento inscrito = treinamento("Treinamento inscrito");
		inscrito.getColaboradores().add(colaborador);
		inscrito = treinamentoRepository.saveAndFlush(inscrito);
		Treinamento aberto = treinamentoRepository.saveAndFlush(treinamento("Treinamento sem inscricao"));

		mockMvc.perform(get("/api/treinamentos")
						.header("Authorization", "Bearer " + token(administrador.getEmail(), "ADMIN")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", hasItem(inscrito.getId().intValue())))
				.andExpect(jsonPath("$[*].id", hasItem(aberto.getId().intValue())));
	}

	@Test
	@Transactional
	void administradorPodeCadastrarUsuario() throws Exception {
		Usuario administrador = criarUsuario("ADMIN");
		String email = UUID.randomUUID() + "@example.test";
		String payload = """
				{
				  "nome": "Novo colaborador",
				  "email": "%s",
				  "senha": "senha-teste",
				  "perfil": "COLABORADOR",
				  "cargo": "Operador",
				  "status": "ATIVO"
				}
				""".formatted(email);

		mockMvc.perform(post("/api/usuarios")
						.header("Authorization", "Bearer " + token(administrador.getEmail(), "ADMIN"))
						.contentType("application/json")
						.content(payload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value(email));
	}

	@Test
	@Transactional
	void colaboradorNaoPodeCriarOuAlterarTreinamentos() throws Exception {
		Usuario colaborador = criarColaborador();
		Treinamento treinamento = treinamentoRepository.saveAndFlush(treinamento("Treinamento protegido"));
		String token = token(colaborador.getEmail(), "COLABORADOR");
		String payload = """
				{
				  "nome": "Novo treinamento",
				  "norma": "NR-06",
				  "descricao": "Descricao",
				  "cargaHoraria": 2,
				  "validade": 12,
				  "participantes": 0,
				  "status": "ATIVO",
				  "usuarioIds": []
				}
				""";

		mockMvc.perform(post("/api/treinamentos")
						.header("Authorization", "Bearer " + token)
						.contentType("application/json")
						.content(payload))
				.andExpect(status().isForbidden());

		mockMvc.perform(put("/api/treinamentos/" + treinamento.getId())
						.header("Authorization", "Bearer " + token)
						.contentType("application/json")
						.content(payload))
				.andExpect(status().isForbidden());
	}

	@Test
	@Transactional
	void administradorPodeGerenciarRiscosEAssociacoesPorUsuario() throws Exception {
		Usuario administrador = criarUsuario("ADMIN");
		Usuario colaborador = criarColaborador();
		String tokenAdmin = token(administrador.getEmail(), "ADMIN");
		String tokenColaborador = token(colaborador.getEmail(), "COLABORADOR");
		String identificador = UUID.randomUUID().toString();
		String nomeRisco = "Choque eletrico " + identificador;
		String payloadRisco = """
				{
				  "setor": "Manutencao %s",
				  "risco": "%s",
				  "nivel": "ALTO",
				  "situacao": "ATIVO",
				  "icone": "bi-lightning-charge"
				}
				""".formatted(identificador, nomeRisco);

		mockMvc.perform(post("/api/riscos")
						.header("Authorization", "Bearer " + tokenAdmin)
						.contentType("application/json")
						.content(payloadRisco))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nivel").value("ALTO"));
		long riscoId = riscoRepository.findAll().stream()
				.filter(risco -> nomeRisco.equals(risco.getRisco()))
				.map(Risco::getId)
				.findFirst()
				.orElseThrow();

		mockMvc.perform(get("/api/riscos")
						.header("Authorization", "Bearer " + tokenAdmin)
						.param("busca", identificador)
						.param("nivel", "ALTO")
						.param("situacao", "ATIVO"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(riscoId));

		mockMvc.perform(put("/api/riscos/" + riscoId)
						.header("Authorization", "Bearer " + tokenAdmin)
						.contentType("application/json")
						.content(payloadRisco.replace("\"ALTO\"", "\"MEDIO\"")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nivel").value("MEDIO"));

		String payloadAssociacao = """
				{
				  "riscos": [
				    {
				      "riscoId": %d,
				      "validade": "2027-12-31",
				      "validado": true
				    }
				  ]
				}
				""".formatted(riscoId);
		mockMvc.perform(put("/api/usuarios/" + colaborador.getId() + "/riscos")
						.header("Authorization", "Bearer " + tokenAdmin)
						.contentType("application/json")
						.content(payloadAssociacao))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].riscoId").value(riscoId))
				.andExpect(jsonPath("$[0].validado").value(true))
				.andExpect(jsonPath("$[0].validade").value("2027-12-31"));

		mockMvc.perform(get("/api/usuarios/me/riscos")
						.header("Authorization", "Bearer " + tokenColaborador))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].risco").value(nomeRisco));

		mockMvc.perform(post("/api/riscos")
						.header("Authorization", "Bearer " + tokenColaborador)
						.contentType("application/json")
						.content(payloadRisco))
				.andExpect(status().isForbidden());
	}

	@Test
	@Transactional
	void colaboradorSalvaInspecaoERespostasNaMesmaTransacaoComDatasIso() throws Exception {
		Usuario colaborador = criarColaborador();
		String authorization = "Bearer " + token(colaborador.getEmail(), "COLABORADOR");
		String payload = """
				{
				  "data": "2026-10-06",
				  "tipoId": "onibus",
				  "tipo": "Pré-operacional",
				  "local": "Garagem Austin",
				  "setor": "Operacional",
				  "numeroOnibus": "3021",
				  "placa": "RIO-3A21",
				  "responsavel": "Responsável de teste",
				  "observacoesGerais": "Inspeção de teste",
				  "respostas": [
				    {
				      "itemId": "pneus",
				      "descricao": "Pneus em condições adequadas",
				      "status": "naoConforme",
				      "observacao": "Desgaste irregular.",
				      "prioridade": "Alta",
				      "prazoCorrecao": "2026-10-13",
				      "responsavelCorrecao": "Manutenção"
				    },
				    {
				      "itemId": "freios",
				      "descricao": "Sistema de freios funcionando",
				      "status": "pendente",
				      "observacao": "",
				      "prioridade": "Média",
				      "prazoCorrecao": null,
				      "responsavelCorrecao": ""
				    }
				  ]
				}
				""";

		mockMvc.perform(post("/api/inspecoes")
						.header("Authorization", authorization)
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data").value("2026-10-06"))
				.andExpect(jsonPath("$.tipoId").value("onibus"))
				.andExpect(jsonPath("$.respostas.length()").value(2))
				.andExpect(jsonPath("$.respostas[0].prazoCorrecao").value("2026-10-13"))
				.andExpect(jsonPath("$.naoConformes").value(1));

		var salvas = inspecaoRepository.findAllByUsuarioIdOrderByDataDescIdDesc(colaborador.getId());
		org.junit.jupiter.api.Assertions.assertEquals(1, salvas.size());
		org.junit.jupiter.api.Assertions.assertEquals(2, salvas.getFirst().getRespostas().size());

		Long inspecaoId = salvas.getFirst().getId();
		mockMvc.perform(put("/api/inspecoes/" + inspecaoId)
						.header("Authorization", authorization)
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload.replace("\"2026-10-06\"", "\"2026-10-07\"")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("2026-10-07"))
				.andExpect(jsonPath("$.respostas.length()").value(2));

		mockMvc.perform(get("/api/inspecoes")
						.header("Authorization", authorization))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].data").value("2026-10-07"));
	}

	@Test
	@Transactional
	void colaboradorNaoPodeConsultarInspecaoDeOutroUsuario() throws Exception {
		Usuario proprietario = criarColaborador();
		Usuario outroColaborador = criarColaborador();
		String payload = """
				{
				  "data": "2026-10-06",
				  "tipoId": "garagem",
				  "tipo": "Rotina",
				  "local": "Garagem",
				  "setor": "Operacional",
				  "responsavel": "Responsável de teste",
				  "respostas": [
				    {
				      "itemId": "piso",
				      "descricao": "Piso limpo",
				      "status": "conforme",
				      "observacao": "",
				      "prioridade": "Média",
				      "prazoCorrecao": null,
				      "responsavelCorrecao": ""
				    }
				  ]
				}
				""";

		mockMvc.perform(post("/api/inspecoes")
						.header("Authorization", "Bearer " + token(proprietario.getEmail(), "COLABORADOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isCreated());
		Long id = inspecaoRepository
				.findAllByUsuarioIdOrderByDataDescIdDesc(proprietario.getId())
				.getFirst().getId();

		mockMvc.perform(get("/api/inspecoes/" + id)
						.header("Authorization", "Bearer " + token(outroColaborador.getEmail(), "COLABORADOR")))
				.andExpect(status().isNotFound());
	}

	private Usuario criarColaborador() {
		return criarUsuario("COLABORADOR");
	}

	private Usuario criarUsuario(String perfil) {
		String email = UUID.randomUUID() + "@example.test";
		return usuarioRepository.saveAndFlush(new Usuario(
				"Usuario de teste",
				email,
				passwordEncoder.encode("senha-teste"),
				perfil,
				perfil,
				"ATIVO"));
	}

	private Treinamento treinamento(String nome) {
		return new Treinamento(nome, "NR-06", "Descricao", 2, 12, 0, "ATIVO");
	}

	private String token(String email, String perfil) {
		UserDetails usuario = User.withUsername(email)
				.password("senha-nao-usada")
				.roles(perfil)
				.build();
		return jwtService.generateToken(usuario);
	}
}
