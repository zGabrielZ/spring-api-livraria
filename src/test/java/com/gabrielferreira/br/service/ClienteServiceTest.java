package com.gabrielferreira.br.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.gabrielferreira.br.modelo.Cliente;
import com.gabrielferreira.br.modelo.dto.criar.CriarClienteDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.ClienteDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarClienteDTO;
import com.gabrielferreira.br.repositorio.ClienteRepositorio;

@ExtendWith(SpringExtension.class) // O Spring deve rodar um mini contexto de injeção de dependecia para rodar os testes
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
public class ClienteServiceTest {

	private ClienteService clienteService;
	private ClienteRepositorio clienteRepositorio;
	private EntityManager entityManager;
	
	private CriteriaBuilder criteriaBuilder;
	private CriteriaQuery<Cliente> criteriaQuery;
	private Root<Cliente> root;
	private TypedQuery<Cliente> typedQuery;
	private Predicate predicateNomeCompleto;
	private Predicate predicateDocumento;
	private Predicate predicatePossuiLivro;
	private Predicate predicateTipoDocumentoCodigo;
	
	@BeforeEach
	public void criarInstancias() {
		clienteRepositorio = Mockito.mock(ClienteRepositorio.class);
		entityManager = Mockito.mock(EntityManager.class);
		clienteService = new ClienteService(clienteRepositorio, entityManager);
	}
	
	@BeforeEach
	@SuppressWarnings("unchecked")
	public void criarInstanciasConsultaFiltro() {
		criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
		criteriaQuery = Mockito.mock(CriteriaQuery.class);
		root = Mockito.mock(Root.class);
		typedQuery = Mockito.mock(TypedQuery.class);
		predicateNomeCompleto = Mockito.mock(Predicate.class);
		predicateDocumento = Mockito.mock(Predicate.class);
		predicatePossuiLivro = Mockito.mock(Predicate.class);
		predicateTipoDocumentoCodigo = Mockito.mock(Predicate.class);
	}
	
	@Test
	@DisplayName("Deve cadastrar cliente com todas as infomações preenchidas.")
	public void deveCadastrarCliente() {
		// Cenário 
		
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(null).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(false).tipoDocumentoCodigo(1).build();
		
		// Cliente com id já mockado
		Cliente clienteCriado = Cliente.builder().id(1L).nomeCompleto(criarClienteDTO.getNomeCompleto()).documento(criarClienteDTO.getDocumento())
				.dataNascimento(criarClienteDTO.getDataNascimento()).possuiLivro(criarClienteDTO.getPossuiLivro())
				.tipoDocumento(clienteService.getTipoDocumentoEscolhido(criarClienteDTO.getTipoDocumentoCodigo())).build();
		
		// Mock para retornar o cliente de cima
		when(clienteRepositorio.save(any())).thenReturn(clienteCriado);
		
		// Executando
		Cliente clienteSalvo = clienteService.inserirCliente(criarClienteDTO);
		
		// Verificando se foi invocado o save no service
		verify(clienteRepositorio).save(any());
		
		// Verificando cada atributo
		assertThat(clienteSalvo.getId()).isNotNull();
		assertThat(clienteSalvo.getNomeCompleto()).isEqualTo(criarClienteDTO.getNomeCompleto());
		assertThat(clienteSalvo.getDocumento()).isEqualTo(criarClienteDTO.getDocumento());
		assertThat(clienteSalvo.getDataNascimento()).isEqualTo(criarClienteDTO.getDataNascimento());
		assertThat(clienteSalvo.getPossuiLivro()).isEqualTo(criarClienteDTO.getPossuiLivro());
		assertThat(clienteSalvo.getTipoDocumento().getCodigo()).isEqualTo(criarClienteDTO.getTipoDocumentoCodigo());
	}
	
	@Test
	@DisplayName("Não deve cadastrar cliente pois o tipo de documento inserido é incorreto.")
	public void naoDeveCadastrarCliente() {
		// Cenário 
		
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(null).nomeCompleto("Gabriel Ferreira").documento("56853510038")
					.dataNascimento(LocalDate.now()).possuiLivro(false).tipoDocumentoCodigo(3).build();
		
		// Execução
		Throwable exception = Assertions.assertThrows(RegraDeNegocioException.class, () -> clienteService.inserirCliente(criarClienteDTO));
		
		// Verificação
		assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage("Insira o tipo de documento corretamente.");
		verify(clienteRepositorio,never()).save(any());
	}
	
	@Test
	@DisplayName("Deve atualizar cliente com as informações preenchidas.")
	public void deveAtualizarCliente() {
		// Cenário 
		
		// Form para atualizar cliente
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumentoCodigo(1).build();
				
		// Cliente que foi feito o update com o form de cima
		Cliente clienteAtualizado = Cliente.builder().id(criarClienteDTO.getId()).nomeCompleto(criarClienteDTO.getNomeCompleto())
				.documento(criarClienteDTO.getDocumento())
				.dataNascimento(criarClienteDTO.getDataNascimento()).possuiLivro(criarClienteDTO.getPossuiLivro())
				.tipoDocumento(clienteService.getTipoDocumentoEscolhido(criarClienteDTO.getTipoDocumentoCodigo())).build();
				
		// Mock para retornar o cliente de cima
		when(clienteRepositorio.save(any())).thenReturn(clienteAtualizado);
				
		// Executando
		Cliente clienteAtualizadoBanco = clienteService.inserirCliente(criarClienteDTO);
				
		// Verificando se foi invocado o save no service
		verify(clienteRepositorio).save(any());
				
		// Verificando cada atributo
		assertThat(clienteAtualizadoBanco.getId()).isNotNull();
		assertThat(clienteAtualizadoBanco.getNomeCompleto()).isEqualTo(criarClienteDTO.getNomeCompleto());
		assertThat(clienteAtualizadoBanco.getDocumento()).isEqualTo(criarClienteDTO.getDocumento());
		assertThat(clienteAtualizadoBanco.getDataNascimento()).isEqualTo(criarClienteDTO.getDataNascimento());
		assertThat(clienteAtualizadoBanco.getPossuiLivro()).isEqualTo(criarClienteDTO.getPossuiLivro());
		assertThat(clienteAtualizadoBanco.getTipoDocumento().getCodigo()).isEqualTo(criarClienteDTO.getTipoDocumentoCodigo());
	}
	
	@Test
	@DisplayName("Deve deletar cliente pelo ID dele.")
	public void deveDeletarCliente() {
		// Cenário 
		Cliente cliente = Cliente.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumento(clienteService.getTipoDocumentoEscolhido(1)).build();
		
		// Retornar esse dado no mock do bucar via banco 
		doReturn(Optional.of(cliente)).when(clienteRepositorio).findById(cliente.getId());
		
		// Executando o método
		clienteService.deletar(cliente.getId());
		
		// Verificando
		verify(clienteRepositorio).deleteById(cliente.getId());
	}
	
	@Test
	@DisplayName("Não deve deletar cliente pelo ID, pois foi informado como nulo.")
	public void naoDeveDeletarCliente() {
		// Cenário 
		Cliente cliente = Cliente.builder().build();
		
		// Executando
		Assertions.assertThrows(IllegalArgumentException.class, () -> clienteService.deletar(cliente.getId()));
		
		// Verificando
		verify(clienteRepositorio,never()).delete(cliente);
	}
	
	@Test
	@DisplayName("Deve obter infomações do cliente pelo ID informado.")
	public void deveObterInformacaoCliente() {
		// Cenário
		Cliente cliente = Cliente.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumento(clienteService.getTipoDocumentoEscolhido(1)).build();
		
		// Retornar esse dado no mock do bucar via banco 
		doReturn(Optional.of(cliente)).when(clienteRepositorio).findById(cliente.getId());
		
		// Executando 
		Cliente clienteBuscado = clienteService.getDetalhe(cliente.getId());
		
		// Verificando
		verify(clienteRepositorio).findById(clienteBuscado.getId());
		assertThat(clienteBuscado.getId()).isNotNull();
		assertThat(clienteBuscado.getNomeCompleto()).isEqualTo(cliente.getNomeCompleto());
		assertThat(clienteBuscado.getDocumento()).isEqualTo(cliente.getDocumento());
		assertThat(clienteBuscado.getDataNascimento()).isEqualTo(cliente.getDataNascimento());
		assertThat(clienteBuscado.getPossuiLivro()).isEqualTo(cliente.getPossuiLivro());
		assertThat(clienteBuscado.getTipoDocumento().getCodigo()).isEqualTo(cliente.getTipoDocumento().getCodigo());
	}
	
	@Test
	@DisplayName("Não deve obter informações do cliente pelo ID, pois não foi informado.")
	public void naoDeveObterInformacaoCliente() {
		// Cenário 
		when(clienteRepositorio.findById(anyLong())).thenReturn(Optional.empty());
		
		// Executando 
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class, () -> clienteService.getDetalhe(anyLong()));
				
		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage("Registro não encontrado.");
		verify(clienteRepositorio).findById(anyLong());
	}
	
	@Test
	@DisplayName("Deve mostrar total de clientes com todas as informações.")
	public void deveMostrarTotalDeClientes() {
		// Cenário 
		List<Cliente> clientes = new ArrayList<Cliente>();
		clientes.add(Cliente.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumento(clienteService.getTipoDocumentoEscolhido(1)).build());
		clientes.add(Cliente.builder().id(2L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumento(clienteService.getTipoDocumentoEscolhido(1)).build());
		clientes.add(Cliente.builder().id(3L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumento(clienteService.getTipoDocumentoEscolhido(1)).build());
		
		// Mock com a lista de cima 
		when(clienteRepositorio.findAll()).thenReturn(clientes);
		
		// Executando 
		List<ClienteDTO> clienteDTOs = clienteService.mostrarClientes();
		
		// Verificando 
		assertThat(!clienteDTOs.isEmpty()).isTrue();
		assertThat(clienteDTOs.size()).isEqualTo(3);
	}
	
	@Test
	@DisplayName("Não deve mostrar total de clientes pois não encontrou nenhum.")
	public void naoDeveMostrarTotalDeClientes() {
		// Cenário
		// Mock para retornar uma lista vazia
		when(clienteRepositorio.findAll()).thenReturn(new ArrayList<>());
		
		// Executando 
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class,
				() -> clienteService.mostrarClientes());
	
		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage("Nenhum cliente encontrado.");
	}
	
	@Test
	@DisplayName("Deve retornar uma lista de clientes com os filtros selecionados.")
	public void deveRetornarClientesFiltros() {
		// Cenário
		ProcurarClienteDTO procurarClienteDTO = ProcurarClienteDTO.builder()
				.nomeCompleto("Gab")
				.documento("56853510038")
				.possuiLivro(true)
				.tipoDocumentoCodigo(1).build();
		
		List<Cliente> clientes = new ArrayList<Cliente>();
		clientes.add(Cliente.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumento(clienteService.getTipoDocumentoEscolhido(1)).build());
		clientes.add(Cliente.builder().id(2L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumento(clienteService.getTipoDocumentoEscolhido(1)).build());
		clientes.add(Cliente.builder().id(3L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumento(clienteService.getTipoDocumentoEscolhido(1)).build());
		
		// Incializando os mocks
		condicaoConsultaTesteMock(procurarClienteDTO);
		
		// Mocks dos filtros
		when(criteriaBuilder.like(root.get("nomeCompleto"), "%" + procurarClienteDTO.getNomeCompleto() + "%")).thenReturn(predicateNomeCompleto);
		when(criteriaBuilder.equal(root.get("documento"), procurarClienteDTO.getDocumento())).thenReturn(predicateDocumento);
		when(criteriaBuilder.equal(root.get("possuiLivro"), procurarClienteDTO.getPossuiLivro())).thenReturn(predicatePossuiLivro);
		when(criteriaBuilder.equal(root.get("tipoDocumento"), procurarClienteDTO.getTipoDocumentoCodigo())).thenReturn(predicateTipoDocumentoCodigo);
		
		// Mock com a lista de cima
		when(typedQuery.getResultList()).thenReturn(clientes);
		
		// Executando
		List<ClienteDTO> clientesDtos = clienteService.clientesFiltros(procurarClienteDTO);
				
		// Verificando
		assertThat(clientesDtos).hasSize(3);
		
	}
	
	@Test
	@DisplayName("Não deve retornar uma lista de clientes, pois não encontrou nenhum cliente.")
	public void naoDeveRetornarClientesFiltros() {
		// Cenário
		ProcurarClienteDTO procurarClienteDTO = ProcurarClienteDTO.builder()
				.nomeCompleto("Gab")
				.documento("56853510038")
				.possuiLivro(true)
				.tipoDocumentoCodigo(1).build();
				
				
		// Incializando os mocks
		condicaoConsultaTesteMock(procurarClienteDTO);
				
		// Mocks dos filtros
		when(criteriaBuilder.like(root.get("nomeCompleto"), "%" + procurarClienteDTO.getNomeCompleto() + "%")).thenReturn(predicateNomeCompleto);
		when(criteriaBuilder.equal(root.get("documento"), procurarClienteDTO.getDocumento())).thenReturn(predicateDocumento);
		when(criteriaBuilder.equal(root.get("possuiLivro"), procurarClienteDTO.getPossuiLivro())).thenReturn(predicatePossuiLivro);
		when(criteriaBuilder.equal(root.get("tipoDocumento"), procurarClienteDTO.getTipoDocumentoCodigo())).thenReturn(predicateTipoDocumentoCodigo);
				
		// Mock com lista vazia
		when(typedQuery.getResultList()).thenReturn(new ArrayList<>());
				
		// Executando
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class,
								() -> clienteService.clientesFiltros(procurarClienteDTO));
						
		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage("Nenhum cliente encontrado.");
	}
	
	private void condicaoConsultaTesteMock(ProcurarClienteDTO procurarClienteDTO) {
		when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(Cliente.class)).thenReturn(criteriaQuery);
		when(criteriaQuery.from(Cliente.class)).thenReturn(root);
		when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
	}
}
