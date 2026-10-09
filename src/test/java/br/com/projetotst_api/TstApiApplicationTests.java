package br.com.projetotst_api;

import br.com.projetotst_api.entity.Risco;
import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.entity.Epis;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.EpisRepository;
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
