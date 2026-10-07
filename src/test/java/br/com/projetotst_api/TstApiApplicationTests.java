package br.com.projetotst_api;

import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.entity.Usuario;
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
