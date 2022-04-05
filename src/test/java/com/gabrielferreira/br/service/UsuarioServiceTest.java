package com.gabrielferreira.br.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.UsuarioDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarUsuarioDTO;
import com.gabrielferreira.br.repositorio.UsuarioRepositorio;
import com.gabrielferreira.br.utils.ValidacaoFormatacao;

@ExtendWith(SpringExtension.class) // O Spring deve rodar um mini contexto de injeção de dependecia para rodar os testes
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
public class UsuarioServiceTest {
	
	private static String USUARIO_MSG = "Usuário";
	
	private UsuarioService usuarioService;
	
	private UsuarioRepositorio usuarioRepositorio;
	
	private EntityManager entityManager;
	
	private CriarUsuarioDTO criarUsuarioDTO;
	
	private CriteriaBuilder criteriaBuilder;
	
	private CriteriaQuery<Usuario> criteriaQuery;
	
	private Root<Usuario> root;
	
	private TypedQuery<Usuario> typedQuery;
	
	private Predicate predicateAutor;
	private Predicate predicateDataNascimentoInicio;
	private Predicate predicateDataNascimentoFinal;
	
	@BeforeEach
	public void criarInstancias() {
		usuarioRepositorio = Mockito.mock(UsuarioRepositorio.class);
		entityManager = Mockito.mock(EntityManager.class);
		usuarioService = new UsuarioService(usuarioRepositorio,entityManager);
		
		criarUsuarioDTO = CriarUsuarioDTO.builder().id(null).autor("José Da Silva").dataNascimento(new Date()).build();
	}
	
	@BeforeEach
	@SuppressWarnings("unchecked")
	public void criarInstanciasConsulta() {
		criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
		criteriaQuery = Mockito.mock(CriteriaQuery.class);
		root = Mockito.mock(Root.class);
		predicateAutor = Mockito.mock(Predicate.class);
		predicateDataNascimentoInicio = Mockito.mock(Predicate.class);
		predicateDataNascimentoFinal = Mockito.mock(Predicate.class);
		typedQuery = Mockito.mock(TypedQuery.class);
	}
	
	@Test
	@DisplayName("Deve inserir um usuário com todas as informações preenchidas.")
	public void deveInserirUsuario() {
		// Cenário já foi construido no criarInstancias()
		
		// Criar a entidade com o id já mockado
		Usuario usuarioCriado = Usuario.builder().id(34L).autor(criarUsuarioDTO.getAutor()).dataNascimento(criarUsuarioDTO.getDataNascimento()).build();
		
		// Quando for salvar no repositorio, retornar o usuário criado ja com o id embutido
		when(usuarioRepositorio.save(any())).thenReturn(usuarioCriado);
		
		// Executando o método
		Usuario usuarioSalvo = usuarioService.inserir(criarUsuarioDTO);
		
		// Verificando se foi invocado o save no service
		verify(usuarioRepositorio).save(any());
		
		// Verificando 
		assertThat(usuarioSalvo.getId()).isNotNull();
		assertThat(usuarioSalvo.getAutor()).isEqualTo(criarUsuarioDTO.getAutor());
		assertThat(usuarioSalvo.getDataNascimento()).isEqualTo(criarUsuarioDTO.getDataNascimento());
		
	}
	
	@Test
	@DisplayName("Deve verificar casos da verificação do nome do usuário informado.")
	public void deveVerificarFormatoNome() {
		// Cenário 
		String nome1 = " Gabriel Ferreira ";
		String nome2 = "gAbrIel ferreira ";
		String nome3 = "GABRIEL FERREIRA";
		String nome4 = "gabriel";
		String nome5 = "GABRIEL";
		String nome6 = "GABRIEL         ";
		String nome7 = "      GABRIEL      ";
		String nome8 = "      GABRIEL      FERREIRA        ";
		String nome9 = "      GABRIEL      FERREIRA        silva";
		
		// Executando o método do formato 
		String nomeF1 = ValidacaoFormatacao.getFormatacaoNome(nome1);
		String nomeF2 = ValidacaoFormatacao.getFormatacaoNome(nome2);
		String nomeF3 = ValidacaoFormatacao.getFormatacaoNome(nome3);
		String nomeF4 = ValidacaoFormatacao.getFormatacaoNome(nome4);
		String nomeF5 = ValidacaoFormatacao.getFormatacaoNome(nome5);
		String nomeF6 = ValidacaoFormatacao.getFormatacaoNome(nome6);
		String nomeF7 = ValidacaoFormatacao.getFormatacaoNome(nome7);
		String nomeF8 = ValidacaoFormatacao.getFormatacaoNome(nome8);
		String nomeF9 = ValidacaoFormatacao.getFormatacaoNome(nome9);
		
		// Verificando
		assertThat(nomeF1).isEqualTo("Gabriel Ferreira");
		assertThat(nomeF2).isEqualTo("Gabriel Ferreira");
		assertThat(nomeF3).isEqualTo("Gabriel Ferreira");
		assertThat(nomeF4).isEqualTo("Gabriel");
		assertThat(nomeF5).isEqualTo("Gabriel");
		assertThat(nomeF6).isEqualTo("Gabriel");
		assertThat(nomeF7).isEqualTo("Gabriel");
		assertThat(nomeF8).isEqualTo("Gabriel Ferreira");
		assertThat(nomeF9).isEqualTo("Gabriel Ferreira Silva");
	}
	
	@Test
	@DisplayName("Deve atualizar um usuário com todas as informações preenchidas.")
	public void deveAtualizarUsuario() {
		
		// Cenário
		// Criar o nosso usuário para fazer o update
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(11L).autor("Teste 123").dataNascimento(new Date()).build();
		
		// Criar a entidade que já foi feito o update
		Usuario usuarioAtualizado = Usuario.builder().id(criarUsuarioDTO.getId()).autor(criarUsuarioDTO.getAutor()).dataNascimento(criarUsuarioDTO.getDataNascimento()).build();
		
		// Quando for verificar o método de autor ja cadastrado, deve retornar null, ou seja não existe autor repetido
		when(usuarioRepositorio.buscarAutorUsuarioQuandoForAtualizar(anyString(), anyLong())).thenReturn(null);
		
		// Quando for atualizar no repositorio, retornar o usuário que queria já salvar
		when(usuarioRepositorio.save(any())).thenReturn(usuarioAtualizado);
		
		// Executando o método
		Usuario usuarioParaAtualizar = usuarioService.inserir(criarUsuarioDTO);
		
		// Verificando se foi invocado o save no service
		verify(usuarioRepositorio).save(any());
		
		// Verificando 
		assertThat(usuarioParaAtualizar.getId()).isNotNull();
		assertThat(usuarioParaAtualizar.getAutor()).isEqualTo(usuarioAtualizado.getAutor());
		assertThat(usuarioParaAtualizar.getDataNascimento()).isEqualTo(usuarioAtualizado.getDataNascimento());
		
	}
	
	@Test
	@DisplayName("Deve deletar o usuário pelo id informado.")
	public void deveDeletarUsuario() {

		// Cenário
		Usuario usuario = Usuario.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		
		// Executando método 
		usuarioService.deletar(usuario.getId(),anyString());
		
		// Verificação
		verify(usuarioRepositorio).deleteById(usuario.getId());
		
	}
	
	@Test
	@DisplayName("Não deve inserir usuário pois o autor já foi cadastrado anteriomente.")
	public void naoDeveInserirUsuarioAutorRepetido() {
		// Cenário já foi construido no criarInstancias()
		
		// Quando verificar qualquer coisa no verificar autor existente, retorna como true
		when(usuarioRepositorio.existsByAutor(criarUsuarioDTO.getAutor())).thenReturn(true);
		
		// Execução
		Throwable exception = Assertions.assertThrows(RegraDeNegocioException.class, () -> usuarioService.inserir(criarUsuarioDTO));
		
		// Verificação
		assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage(exception.getMessage());
		verify(usuarioRepositorio,never()).save(any());
	}
	
	@Test
	@DisplayName("Não deve atualizar o usuário, pois já tem o autor cadastrado.")
	public void naoDeveAtualizarUsuarioAutorRepetido() {
		
		// Cénario
		// Criando o nosso usuario para fazer o update
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(11L).autor("Teste 123").dataNascimento(new Date()).build();

		// Entidade que já esta salva no banco do mock
		Usuario usuarioSalvo = Usuario.builder().id(50L).autor("Teste 123").dataNascimento(new Date()).build();

		// Quando for verificar o autor cadastrado, é pra retorna como verdadeiro, ou seja existe autor cadastrado
		when(usuarioRepositorio.buscarAutorUsuarioQuandoForAtualizar(anyString(), anyLong()))
				.thenReturn(usuarioSalvo);


		// Execução
		Throwable exception = Assertions.assertThrows(RegraDeNegocioException.class, () -> usuarioService.inserir(criarUsuarioDTO));
				
		// Verificação
		assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage(exception.getMessage());
		verify(usuarioRepositorio,never()).save(any());
	}
	
	@Test
	@DisplayName("Deve buscar usuário com o id informado.")
	public void deveBuscarUsuario() {
		// Cenário
		Usuario usuario = Usuario.builder().id(34L).autor("José da Silva").dataNascimento(new Date()).build();	
		
		// Quando for buscar o usuario com qualquer coisa, tem que retornar o usuario criado de cima
		doReturn(Optional.of(usuario)).when(usuarioRepositorio).findById(anyLong());
		
		// Executando o método de buscar
		Usuario usuarioExistente = usuarioService.getDetalhe(usuario.getId(),USUARIO_MSG);
		
		// Verificando se foi invocado o find by
		verify(usuarioRepositorio).findById(anyLong());
		
		// Verificando se foi igual
		assertThat(usuarioExistente.getId()).isNotNull();
		assertThat(usuarioExistente.getAutor()).isEqualTo(usuario.getAutor());
		assertThat(usuarioExistente.getDataNascimento()).isEqualTo(usuario.getDataNascimento());
		
	}
	
	@Test
	@DisplayName("Deve lançar uma exception, pois não encontrou o usuário.")
	public void naoDeveBuscarUsuario() {
		// Cenário 	
		when(usuarioRepositorio.findById(anyLong())).thenReturn(Optional.empty());
		
		// Executando 
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class, () -> usuarioService.getDetalhe(anyLong(),USUARIO_MSG));
		
		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage(exception.getMessage());
		verify(usuarioRepositorio).findById(anyLong());
	}
	
	@Test
	@DisplayName("Não deve deletar usuário pois o id não foi informado.")
	public void naoDeveDeletarUsuario() {
		
		// Cenário
		Usuario usuario = new Usuario();
		
		// Executando 
		Assertions.assertThrows(IllegalArgumentException.class, () -> usuarioService.deletar(usuario.getId(),USUARIO_MSG));
		
		// Verificando
		verify(usuarioRepositorio,never()).delete(usuario);
	}
	
	@Test
	@DisplayName("Deve mostrar lista de usários cadastrados.")
	public void deveMostrarListaDeUsuarios() {
		// Cenário 
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(Usuario.builder().id(1L).autor("José da Silva").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(2L).autor("Marcos da Silva").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(3L).autor("Natália da Silva").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(4L).autor("Josué da Silva").dataNascimento(new Date()).build());
		
		// Mockando o resultado de usuários 
		when(usuarioRepositorio.findAll()).thenReturn(usuarios);
		
		// Executando o método
		List<UsuarioDTO> usuarioDTOs = usuarioService.mostrarUsuarios();
		
		// Verificando se foi invocado
		verify(usuarioRepositorio).findAll();
		
		// Verificando
		assertThat(usuarioDTOs).hasSize(4);
	}
	
	@Test
	@DisplayName("Não deve mostrar lista de usários pois ninguem foi cadastrado.")
	public void naoDeveMostrarListaDeUsuarios() {
		// Cenário
		when(usuarioRepositorio.findAll()).thenReturn(new ArrayList<>());

		// Executando
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class,
				() -> usuarioService.mostrarUsuarios());

		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage(exception.getMessage());
		verify(usuarioRepositorio).findAll();
	}
	
	// fazer os testes de service, paginacao do controller, e depois comentar o codigo,documentar swagger
	@Test
	@DisplayName("Deve mostrar lista de usuários com todos os parametros informados.")
	public void deveMostrarListaDeUsuariosParametros() throws ParseException {
		
		// Cenário 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ProcurarUsuarioDTO procurarUsuarioDTO = ProcurarUsuarioDTO.builder().autor("Gabriel").dataNascimentoInicio(sdf.parse("04/04/2022"))
					.dataNascimentoFinal(sdf.parse("05/04/2022"))
					.build();
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(Usuario.builder().id(1L).autor("Gabriel 1").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(2L).autor("Gabriel 2").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(3L).autor("Gabriel 3").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(4L).autor("Gabriel 4").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(5L).autor("Gabriel 5").dataNascimento(new Date()).build());
		
		condicaoConsultaTeste(procurarUsuarioDTO);
		
		when(criteriaBuilder.like(root.get("autor"), "%" + procurarUsuarioDTO.getAutor()+ "%")).thenReturn(predicateAutor);
		when(criteriaBuilder.greaterThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.getDataNascimentoInicio())).thenReturn(predicateDataNascimentoInicio);
		when(criteriaBuilder.lessThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.getDataNascimentoFinal())).thenReturn(predicateDataNascimentoFinal);
		when(typedQuery.getResultList()).thenReturn(usuarios);
		
		// Executando
		List<UsuarioDTO> usuarioDTOs = usuarioService.filtroUsuarios(procurarUsuarioDTO);
		
		// Verificando
		assertThat(usuarioDTOs).hasSize(5);
		
	}
	
	@Test
	@DisplayName("Deve mostrar lista de usuários somente a data nascimento final informado.")
	public void deveMostrarListaDeUsuariosParametrosDataFinal() throws ParseException {
		
		// Cenário 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ProcurarUsuarioDTO procurarUsuarioDTO = ProcurarUsuarioDTO.builder().autor(null).dataNascimentoInicio(null)
					.dataNascimentoFinal(sdf.parse("05/04/2022"))
					.build();
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(Usuario.builder().id(1L).autor("Gabriel 1").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(2L).autor("Gabriel 2").dataNascimento(new Date()).build());
		
		condicaoConsultaTeste(procurarUsuarioDTO);
		
		when(criteriaBuilder.lessThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.getDataNascimentoFinal())).thenReturn(predicateDataNascimentoFinal);
		when(typedQuery.getResultList()).thenReturn(usuarios);
		
		// Executando
		List<UsuarioDTO> usuarioDTOs = usuarioService.filtroUsuarios(procurarUsuarioDTO);
		
		// Verificando
		assertThat(usuarioDTOs).hasSize(2);
		
	}
	
	@Test
	@DisplayName("Deve mostrar lista de usuários somente a data nascimento inicial informado.")
	public void deveMostrarListaDeUsuariosParametrosDataInicio() throws ParseException {
		
		// Cenário 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ProcurarUsuarioDTO procurarUsuarioDTO = ProcurarUsuarioDTO.builder().autor(null).dataNascimentoInicio(sdf.parse("05/04/2022"))
					.dataNascimentoFinal(null).build();
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(Usuario.builder().id(1L).autor("Gabriel 1").dataNascimento(new Date()).build());
		usuarios.add(Usuario.builder().id(2L).autor("Gabriel 2").dataNascimento(new Date()).build());
		
		condicaoConsultaTeste(procurarUsuarioDTO);
		
		when(criteriaBuilder.greaterThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.getDataNascimentoInicio())).thenReturn(predicateDataNascimentoInicio);
		when(typedQuery.getResultList()).thenReturn(usuarios);
		
		// Executando
		List<UsuarioDTO> usuarioDTOs = usuarioService.filtroUsuarios(procurarUsuarioDTO);
		
		// Verificando
		assertThat(usuarioDTOs).hasSize(2);
		
	}
	
	@Test
	@DisplayName("Não deve mostrar lista de usuários com os parametros informados pois não encontrou nenhum usuário.")
	public void naoDeveMostrarListaDeUsuariosParametros() throws ParseException{
		// Cenário 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		ProcurarUsuarioDTO procurarUsuarioDTO = ProcurarUsuarioDTO.builder().autor("Gabriel").dataNascimentoInicio(sdf.parse("04/04/2022"))
						.dataNascimentoFinal(sdf.parse("05/04/2022"))
						.build();
		
		condicaoConsultaTeste(procurarUsuarioDTO);
		
		
		when(criteriaBuilder.like(root.get("autor"), "%" + procurarUsuarioDTO.getAutor()+ "%")).thenReturn(predicateAutor);
		when(criteriaBuilder.greaterThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.getDataNascimentoInicio())).thenReturn(predicateDataNascimentoInicio);
		when(criteriaBuilder.lessThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.getDataNascimentoFinal())).thenReturn(predicateDataNascimentoFinal);
		when(typedQuery.getResultList()).thenReturn(new ArrayList<>());
		
		// Executando
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class,
					() -> usuarioService.filtroUsuarios(procurarUsuarioDTO));
		
		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage(exception.getMessage());
	}
	
	@Test
	@DisplayName("Não deve mostrar lista de usuários com os parametros informados pois não a data nascimento inicial é maior do que a final.")
	public void naoDeveMostrarListaDeUsuariosDataIncorreta() throws ParseException{
		// Cenário 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ProcurarUsuarioDTO procurarUsuarioDTO = ProcurarUsuarioDTO.builder().autor("Gabriel").dataNascimentoInicio(sdf.parse("10/04/2022"))
						.dataNascimentoFinal(sdf.parse("05/04/2022"))
						.build();
		
		condicaoConsultaTeste(procurarUsuarioDTO);
		
		// Executando
		Throwable exception = Assertions.assertThrows(RegraDeNegocioException.class,
					() -> usuarioService.filtroUsuarios(procurarUsuarioDTO));
		
		// Verificação
		assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage(exception.getMessage());
	}
	
	private void condicaoConsultaTeste(ProcurarUsuarioDTO procurarUsuarioDTO) {
		when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(Usuario.class)).thenReturn(criteriaQuery);
		when(criteriaQuery.from(Usuario.class)).thenReturn(root);
		when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
	}
}
