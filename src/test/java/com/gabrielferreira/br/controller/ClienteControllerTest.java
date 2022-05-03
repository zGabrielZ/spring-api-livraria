package com.gabrielferreira.br.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Cliente;
import com.gabrielferreira.br.modelo.dto.criar.CriarClienteDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.ClienteDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarClienteDTO;
import com.gabrielferreira.br.modelo.enums.TipoDocumento;
import com.gabrielferreira.br.service.ClienteService;

@SpringBootTest
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
@AutoConfigureMockMvc // Configuração do teste para configurar os objetos
public class ClienteControllerTest {

	private static String API_CLIENTES = "/api/clientes";
	private static MediaType JSON_MEDIATYPE = MediaType.APPLICATION_JSON;
	
	@Autowired
	private MockMvc mockMvc; // Mockando as requisições
	
	@MockBean // Quando o contexto subir, não pode injetar o objeto real, so com os objetos falsos
	private ClienteService clienteService;
	
	@Test
	@DisplayName("Deve inserir cliente com a requisição POST com todas as informações preenchidas.")
	public void deveInserirCliente() throws Exception{
		// Cenário
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(null).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(false).tipoDocumentoCodigo(1).build();
		
		// Criar entidade já com o id mockado
		Cliente clienteCriado = Cliente.builder().id(1L).nomeCompleto(criarClienteDTO.getNomeCompleto()).documento(criarClienteDTO.getDocumento())
				.dataNascimento(criarClienteDTO.getDataNascimento()).possuiLivro(criarClienteDTO.getPossuiLivro())
				.tipoDocumento(TipoDocumento.CPF).build();
		
		when(clienteService.inserirCliente(any())).thenReturn(clienteCriado);
		
		// Transformar o objeto em json
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		
		String json = objectMapper.writeValueAsString(criarClienteDTO);
				
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_CLIENTES).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
						
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(jsonPath("id").value(clienteCriado.getId()))
					.andExpect(jsonPath("nomeCompleto").value(clienteCriado.getNomeCompleto()))
					.andExpect(jsonPath("documento").value(clienteCriado.getDocumento()))
					.andExpect(jsonPath("dataNascimento").value(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(clienteCriado.getDataNascimento())))
					.andExpect(jsonPath("possuiLivro").value(clienteCriado.getPossuiLivro()))
					.andExpect(jsonPath("tipoDocumentoCodigo").value(clienteCriado.getTipoDocumento().getCodigo()));
	}
	
	@Test
	@DisplayName("Não deve inserir cliente pois não tem dados suficientes.")
	public void naoDeveInserirClienteInformacoesVazia() throws Exception{
		// Cenário
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(null).nomeCompleto(null).documento(null)
				.dataNascimento(null).possuiLivro(null).tipoDocumentoCodigo(null).build();
				
		// Transformar o objto em json
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(criarClienteDTO);
						
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_CLIENTES).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
				
		// Verificar a requisição
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("campos",Matchers.hasSize(4)));
	}
	
	@Test
	@DisplayName("Não deve inserir cliente pois o tipo de documento foi invalido.")
	public void naoDeveInserirClienteTipoDocumentoInvalido() throws Exception{
		// Cenário
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(null).nomeCompleto("Gabriel Ferreira").documento("56853510038")
					.dataNascimento(LocalDate.now()).possuiLivro(false).tipoDocumentoCodigo(3).build();
				
		// Executando o inserir do cliente
		when(clienteService.inserirCliente(any())).thenThrow(new RegraDeNegocioException("Insira o tipo de documento corretamente."));
		
		// Transformar o objeto em json
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
				
		String json = objectMapper.writeValueAsString(criarClienteDTO);
						
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_CLIENTES).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
								
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("mensagem", equalTo("Insira o tipo de documento corretamente.")));	
	}
	
	@Test
	@DisplayName("Deve atualizar cliente com informações passadas")
	public void deveAtualizarCliente() throws Exception{
		// Cenário 
		// Cliente já está salvo no banco de dados
		Cliente clienteSalvo = Cliente.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(false).tipoDocumento(TipoDocumento.CPF).build();
		
		// Mock com o retorno do cliente salvo na hora de buscar por id 
		when(clienteService.getDetalhe(anyLong())).thenReturn(clienteSalvo);
		
		// Criando o nosso cliente form para atualizar 
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("81251934056")
				.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumentoCodigo(1).build();
		
		// Cliente já salvo com as informações de cima repassada 
		Cliente clienteAtualizado = Cliente.builder().id(criarClienteDTO.getId()).nomeCompleto(criarClienteDTO.getNomeCompleto())
				.documento(criarClienteDTO.getDocumento()).dataNascimento(criarClienteDTO.getDataNascimento())
				.possuiLivro(criarClienteDTO.getPossuiLivro()).tipoDocumento(TipoDocumento.CPF).build();
		
		// Mock com o cliente de cima para retornar
		when(clienteService.inserirCliente(any())).thenReturn(clienteAtualizado);
		
		// Transformar o objeto em json
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		
		String json = objectMapper.writeValueAsString(criarClienteDTO);
		
		// Criar uma requisição do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_CLIENTES + "/{idCliente}",criarClienteDTO.getId())
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
				
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("id").value(clienteAtualizado.getId()))
				.andExpect(jsonPath("nomeCompleto").value(clienteAtualizado.getNomeCompleto()))
				.andExpect(jsonPath("documento").value(clienteAtualizado.getDocumento()))
				.andExpect(jsonPath("dataNascimento").value(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(clienteAtualizado.getDataNascimento())))
				.andExpect(jsonPath("possuiLivro").value(clienteAtualizado.getPossuiLivro()))
				.andExpect(jsonPath("tipoDocumentoCodigo").value(clienteAtualizado.getTipoDocumento().getCodigo()));
	}
	
	@Test
	@DisplayName("Não deve atualizar cliente pois não encontrou id.")
	public void naoDeveAtualizarClienteIdNaoEncontrado() throws Exception{
		// Cenário 
		Long id = 50L;
		
		// Mock para retornar exception 
		when(clienteService.getDetalhe(id)).thenThrow(new EntidadeNotFoundException("Registro não encontrado."));
		
		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_CLIENTES + "/{idCliente}",id).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("mensagem", equalTo("Registro não encontrado.")));
	}
	
	@Test
	@DisplayName("Não deve atualizar cliente pois só tem informações vazias.")
	public void naoDeveAtualizarClienteInformacoesVazia() throws Exception{
		// Criando o nosso cliente form para atualizar 
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(1L).nomeCompleto(null).documento(null)
					.dataNascimento(null).possuiLivro(null).tipoDocumentoCodigo(null).build();
				
		// Transformar o objeto em json
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(criarClienteDTO);
				
		// Criar uma requisição do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_CLIENTES + "/{idCliente}",criarClienteDTO.getId())
					.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("campos",Matchers.hasSize(4)));
	}
	
	@Test
	@DisplayName("Não deve atualizar cliente pois o tipo de documento é invalido.")
	public void naoDeveAtualizarClienteTipoDocumentoInvalido() throws Exception{
		// Cenário 
		// Cliente já está salvo no banco de dados
		Cliente clienteSalvo = Cliente.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
					.dataNascimento(LocalDate.now()).possuiLivro(false).tipoDocumento(TipoDocumento.CPF).build();
				
		// Mock com o retorno do cliente salvo na hora de buscar por id 
		when(clienteService.getDetalhe(anyLong())).thenReturn(clienteSalvo);
				
		// Criando o nosso cliente form para atualizar 
		CriarClienteDTO criarClienteDTO = CriarClienteDTO.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("81251934056")
					.dataNascimento(LocalDate.now()).possuiLivro(true).tipoDocumentoCodigo(3).build();
				
		// Mock com o cliente de cima para retornar
		when(clienteService.inserirCliente(any())).thenThrow(new RegraDeNegocioException("Insira o tipo de documento corretamente."));
				
		// Transformar o objeto em json
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
				
		String json = objectMapper.writeValueAsString(criarClienteDTO);
				
		// Criar uma requisição do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_CLIENTES + "/{idCliente}",criarClienteDTO.getId())
					.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
						
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Insira o tipo de documento corretamente.")));
	}
	
	@Test
	@DisplayName("Deve deletar cliente pelo ID.")
	public void deveDeletarCliente() throws Exception{
		// Cenário
		Cliente cliente = Cliente.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(false).tipoDocumento(TipoDocumento.CPF).build();
		
		// Mock para retornar o cliente de cima 
		when(clienteService.getDetalhe(anyLong())).thenReturn(cliente);
		
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_CLIENTES + "/{idCliente}",cliente.getId())
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Não deve deletar cliente pois não encontrou o ID.")
	public void naoDeveDeletarClienteIdNaoEncontrado() throws Exception{
		// Cenário
		Long id = 50L;
		
		// Mock para retornar uma exception
		when(clienteService.getDetalhe(id)).thenThrow(new EntidadeNotFoundException("Registro não encontrado."));
		
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_CLIENTES + "/{idCliente}",id).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("mensagem", equalTo("Registro não encontrado.")));
	}
	
	@Test
	@DisplayName("Deve obter informações do cliente pelo ID.")
	public void deveObterInformacao() throws Exception{
		// Cenário
		Cliente cliente = Cliente.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("56853510038")
				.dataNascimento(LocalDate.now()).possuiLivro(false).tipoDocumento(TipoDocumento.CPF).build();
		
		// Mock para retornar o cliente de cima
		when(clienteService.getDetalhe(anyLong())).thenReturn(cliente);
		
		// Criar uma requisição do tipo get 
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_CLIENTES + "/{idCliente}",cliente.getId())
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("id").value(cliente.getId()))
					.andExpect(jsonPath("nomeCompleto").value(cliente.getNomeCompleto()))
					.andExpect(jsonPath("documento").value(cliente.getDocumento()))
					.andExpect(jsonPath("dataNascimento").value(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(cliente.getDataNascimento())))
					.andExpect(jsonPath("possuiLivro").value(cliente.getPossuiLivro()))
					.andExpect(jsonPath("tipoDocumentoCodigo").value(cliente.getTipoDocumento().getCodigo()));
	}
	
	@Test
	@DisplayName("Não deve obter informação do cliente pelo ID pois não encontrou o cliente.")
	public void naoDeveObterInformacao() throws Exception{
		// Cenário
		Long id = 50L;
		
		// Mock para retornar uma exception
		when(clienteService.getDetalhe(id)).thenThrow(new EntidadeNotFoundException("Registro não encontrado."));
				
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_CLIENTES + "/{idCliente}",id).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
								
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("mensagem", equalTo("Registro não encontrado.")));
		
	}
	
	@Test
	@DisplayName("Deve mostrar clientes com filtros com todos os parametros informados.")
	public void deveMostrarClientesPaginados() throws Exception{
		// Cenário
		ProcurarClienteDTO procurarClienteDTO = ProcurarClienteDTO.builder().nomeCompleto("Gab")
				.documento("76965519061").possuiLivro(true).tipoDocumentoCodigo(1).build();
		
		// Fazer a listagem de dados encontrados
		List<ClienteDTO> clientes = new ArrayList<ClienteDTO>();
		clientes.add(ClienteDTO.builder().id(1L).nomeCompleto("Gabriel Ferreira").documento("76965519061")
				.dataNascimento(LocalDate.now()).possuiLivro(true)
				.tipoDocumentoCodigo(TipoDocumento.CPF.getCodigo()).build());
		clientes.add(ClienteDTO.builder().id(2L).nomeCompleto("Gabriel Ferreira").documento("76965519061")
				.dataNascimento(LocalDate.now()).possuiLivro(true)
				.tipoDocumentoCodigo(TipoDocumento.CPF.getCodigo()).build());
		clientes.add(ClienteDTO.builder().id(3L).nomeCompleto("Gabriel Ferreira").documento("76965519061")
				.dataNascimento(LocalDate.now()).possuiLivro(true)
				.tipoDocumentoCodigo(TipoDocumento.CPF.getCodigo()).build());
		
		// Mock para retornar clientes de cima 
		when(clienteService.clientesFiltros(any())).thenReturn(clientes);
		
		
		// Montar a query paginação
		String queryPaginacao = API_CLIENTES + "?nomeCompleto=" + procurarClienteDTO.getNomeCompleto()
				+ "&documento=" + procurarClienteDTO.getDocumento()
				+ "&possuiLivro=" + procurarClienteDTO.getPossuiLivro()
				+ "&tipoDocumentoCodigo=" + procurarClienteDTO.getTipoDocumentoCodigo()
				+ "&pagina="+0
				+ "&totalRegistro="+2;
		
		// Criar requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryPaginacao).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("source",Matchers.hasSize(3))) // Total de registros que foi buscado sem filtro
				.andExpect(jsonPath("pageSize").value(2)) // Quantidade de registro com o filtro
				.andExpect(jsonPath("pageList",Matchers.hasSize(2))) // Total de registro que foi buscado com filtro
				.andExpect(jsonPath("page").value(0)); // Página que foi informado no parametro
	}
	
	@Test
	@DisplayName("Não deve mostrar clientes pois não foi encontrado nenhum.")
	public void naoDeveMostrarClientesPaginados() throws Exception{
		// Cenário 
		ProcurarClienteDTO procurarClienteDTO = ProcurarClienteDTO.builder().nomeCompleto("Gab")
				.documento("76965519061").possuiLivro(true).tipoDocumentoCodigo(1).build();
				
		// Executando o buscar do usuários filtro
		when(clienteService.clientesFiltros(any())).thenThrow(new EntidadeNotFoundException("Nenhum cliente encontrado."));
				
		// Montar a query paginação
		String queryPaginacao = API_CLIENTES + "?nomeCompleto=" + procurarClienteDTO.getNomeCompleto()
					+ "&documento=" + procurarClienteDTO.getDocumento()
					+ "&possuiLivro=" + procurarClienteDTO.getPossuiLivro()
					+ "&tipoDocumentoCodigo=" + procurarClienteDTO.getTipoDocumentoCodigo()
					+ "&pagina="+0
					+ "&totalRegistro="+2;
				
		// Criar requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryPaginacao).accept(JSON_MEDIATYPE)
						.contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("mensagem", equalTo("Nenhum cliente encontrado.")));
	}
}
