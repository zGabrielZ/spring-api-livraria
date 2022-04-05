package com.gabrielferreira.br.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarLivroDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.LivroDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.UsuarioDTO;
import com.gabrielferreira.br.service.LivroService;

@SpringBootTest
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
@AutoConfigureMockMvc // Configuração do teste para configurar os objetos
public class LivroControllerTest {

	private static String LIVRO_MSG = "Livro";
	private static String API_LIVROS = "/api/livros";
	private static MediaType JSON_MEDIATYPE = MediaType.APPLICATION_JSON;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private MockMvc mockMvc; // Mockando as requisições
	
	@MockBean // Quando o contexto subir, não pode injetar o objeto real, so com os objetos falsos
	private LivroService livroService;
	
	private Usuario usuarioCriado;
	
	@BeforeEach
	public void criarInstancias() throws Exception{
		usuarioCriado = Usuario.builder().id(1L).autor("José da Silva").dataNascimento(sdf.parse("26/12/1994")).build();
	}
	
	@Test
	@DisplayName("Deve inserir o livro utilizando a requisição POST com sucesso.")
	public void deveInserirLivro() throws Exception{
		
		// Cénario
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();
		
		// Criar a entidade com o id já mockado
		Livro livroCriado = Livro.builder().id(22L).usuario(usuarioCriado).isbn(criarLivroDTO.getIsbn()).titulo(criarLivroDTO.getTitulo())
				.subtitulo(criarLivroDTO.getSubtitulo()).sinopse(criarLivroDTO.getSinopse()).build();
		
		// Executando o inserir do livro
		when(livroService.inserir(any())).thenReturn(livroCriado);
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").value(livroCriado.getId()))
			.andExpect(jsonPath("titulo").value(livroCriado.getTitulo()))
			.andExpect(jsonPath("idUsuario").value(livroCriado.getUsuario().getId()))
			.andExpect(jsonPath("isbn").value(livroCriado.getIsbn()))
			.andExpect(jsonPath("subtitulo").value(livroCriado.getSubtitulo()))
			.andExpect(jsonPath("sinopse").value(livroCriado.getSinopse()));
		
	}
	
	@Test
	@DisplayName("Não deve inserir o livro pois não tem dados suficiente para criação do livro.")
	public void naoDeveInserirLivroNaoTemDadoSuficiente() throws Exception{
		
		// Cenário
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(null).isbn(null).titulo(null).subtitulo(null).sinopse(null).build();
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("campos", Matchers.hasSize(5)));
	
	}
	
	@Test
	@DisplayName("Não deve inserir o livro, pois já tem o isbn já cadastrado por outro.")
	public void naoDeveInserirLivroPoisTemIsbnCadastrado() throws Exception{
		
		// Cenário
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();
		
		// Executando o inserir do livro
		when(livroService.inserir(any())).thenThrow(new RegraDeNegocioException("Este ISBN já foi cadastrado por outro livro."));
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
				
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Este ISBN já foi cadastrado por outro livro.")));
	}
	
	
	@Test
	@DisplayName("Não deve inserir o livro, pois já tem o título já cadastrado por outro.")
	public void naoDeveInserirLivroPoisTemTituloCadastrado() throws Exception {
		
		// Cenário
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001")
				.titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();

		// Executando o inserir do livro
		when(livroService.inserir(any()))
				.thenThrow(new RegraDeNegocioException("Este Título já foi cadastrado por outro livro."));

		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);

		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE).content(json);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Este Título já foi cadastrado por outro livro.")));
	}
	
	@Test
	@DisplayName("Não deve inserir o livro, pois não encontrou o usuário informado.")
	public void naoDeveInserirLivroPoisNaoEncontrouUsuario() throws Exception {
		
		// Cenário
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001")
				.titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();

		// Executando o buscar do usuario
		when(livroService.inserir(any()))
				.thenThrow(new EntidadeNotFoundException("Usuário não encontrado."));

		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);

		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE).content(json);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Usuário não encontrado.")));
	}
	
	@Test
	@DisplayName("Não deve inserir livro pois tem mais de 13 caracteres no campo ISBN.")
	public void naoDeveInserirLivroCaracteresIsbn() throws Exception{
		// Cénario
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId())
				.isbn("0014309823480932840238048230").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();
	
		// Executando o verificar campo ISBN 
		when(livroService.inserir(any())).thenThrow(new RegraDeNegocioException("O limite de caracteres do ISBN é até 13."));
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
						.contentType(JSON_MEDIATYPE).content(json);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("O limite de caracteres do ISBN é até 13.")));
	}
	
	@Test
	@DisplayName("Não deve inserir livro pois o ISBN tem que ser numérico.")
	public void naoDeveInserirLivroNumericoIsbn() throws Exception{
		// Cénario
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId())
					.isbn("asasaasdfdgdhrtghdhdrthrth").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();
			
		// Executando o verificar campo ISBN 
		when(livroService.inserir(any())).thenThrow(new RegraDeNegocioException("É necessário inserir somente numérico para o ISBN."));
				
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
				
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
						.contentType(JSON_MEDIATYPE).content(json);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("mensagem", equalTo("É necessário inserir somente numérico para o ISBN.")));
	}
	
	@Test
	@DisplayName("Deve buscar o livro com o id informado.")
	public void deveBuscarLivro() throws Exception {
		// Cenário 
		Usuario usuario = Usuario.builder().id(133L).autor("Teste usuário").dataNascimento(new Date()).build();
		Livro livro = Livro.builder().id(12L).usuario(usuario).isbn("001").titulo("Teste Livro Gabriel").subtitulo("Teste teste Gabriel")
				.sinopse("Teste sinopse gabriel").build();
		
		// Executando o buscar do livro
		when(livroService.getDetalhe(livro.getId(),LIVRO_MSG)).thenReturn(livro);
		
		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_LIVROS + "/{idLivro}",livro.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(livro.getId()))
				.andExpect(jsonPath("titulo").value(livro.getTitulo()))
				.andExpect(jsonPath("isbn").value(livro.getIsbn()))
				.andExpect(jsonPath("subtitulo").value(livro.getSubtitulo()))
				.andExpect(jsonPath("sinopse").value(livro.getSinopse()))
				.andExpect(jsonPath("usuarioDto.autor").value(livro.getUsuario().getAutor()));
	}
	
	@Test
	@DisplayName("Não deve buscar o livro, pois não encontrou informações dele.")
	public void naoDeveBuscarLivro() throws Exception {
		
		// Cenário
		Long idLivro = 122L;

		// Executando o buscar do livro
		when(livroService.getDetalhe(idLivro,LIVRO_MSG)).thenThrow(new EntidadeNotFoundException("Livro não encontrado."));

		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_LIVROS + "/{idLivro}",idLivro).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Livro não encontrado.")));
	}
	
	@Test
	@DisplayName("Deve deletar livro pelo id informado.")
	public void deveDeletarLivro() throws Exception {
		// Cenário 
		Usuario usuario = Usuario.builder().id(133L).autor("Teste usuário").dataNascimento(new Date()).build();
		Livro livro = Livro.builder().id(12L).usuario(usuario).isbn("001").titulo("Teste Livro Gabriel").subtitulo("Teste teste Gabriel")
				.sinopse("Teste sinopse gabriel").build();
		
		// Executando o deletar do livro
		when(livroService.getDetalhe(livro.getId(),LIVRO_MSG)).thenReturn(livro);
		
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_LIVROS + "/{idLivro}",livro.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNoContent());
						
	}
	
	@Test
	@DisplayName("Não deve deletar livro pelo id informado pois não encontrou o livro.")
	public void naoDeveDeletarLivro() throws Exception {
		// Cenário 
		Long idLivro = 150L;
		
		// Executando o deletar do livro
		when(livroService.getDetalhe(idLivro,LIVRO_MSG)).thenThrow(new EntidadeNotFoundException("Livro não encontrado."));
		
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_LIVROS + "/{idLivro}",idLivro).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Livro não encontrado.")));
						
	}
	
	@Test
	@DisplayName("Deve atualizar o livro pelo id informado e campos encontrado.")
	public void deveAtualizarLivro() throws Exception{
		
		// Cénario
		
		// Criar a entidade que já está salva no banco do mock
		Livro livroJaSalvo = Livro.builder().id(50L).usuario(usuarioCriado).isbn("001").titulo("Teste teste")
					.subtitulo("Teste subtitulo").sinopse("Teste sinopse").build();
		
		// Criando o nosso livro para fazer o update 
		Usuario usuarioAtualizar = Usuario.builder().id(30L).autor("Teste autor").dataNascimento(new Date()).build();
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(50L).idUsuario(usuarioAtualizar.getId()).isbn("002").titulo("Teste Livro atualizar")
				.subtitulo("Teste subtitulo atualizar").sinopse("Teste sinopse atualizar").build();
		
		// Criar a entidade que já foi feito o update
		Livro livroAtualizado = Livro.builder().id(criarLivroDTO.getId()).usuario(usuarioAtualizar).isbn(criarLivroDTO.getIsbn()).titulo(criarLivroDTO.getTitulo())
							.subtitulo(criarLivroDTO.getSubtitulo()).sinopse(criarLivroDTO.getSinopse()).build();
		
		// Executando o buscar do livro
		when(livroService.getDetalhe(livroJaSalvo.getId(),LIVRO_MSG)).thenReturn(livroJaSalvo);
		
		// Executando o atualizar do livro
		when(livroService.inserir(any())).thenReturn(livroAtualizado);
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisição do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_LIVROS + "/{idLivro}",criarLivroDTO.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("id").value(livroAtualizado.getId()))
			.andExpect(jsonPath("titulo").value(livroAtualizado.getTitulo()))
			.andExpect(jsonPath("idUsuario").value(livroAtualizado.getUsuario().getId()))
			.andExpect(jsonPath("isbn").value(livroAtualizado.getIsbn()))
			.andExpect(jsonPath("subtitulo").value(livroAtualizado.getSubtitulo()))
			.andExpect(jsonPath("sinopse").value(livroAtualizado.getSinopse()));
		
	}
	
	@Test
	@DisplayName("Não deve atualizar o livro pois não encontrou o id como parametro.")
	public void naoDeveAtualizarLivroIdParametroNaoInformado() throws Exception{
		
		// Cénario
		
		// Criando o nosso livro para fazer o update 
		Usuario usuarioAtualizar = Usuario.builder().id(30L).autor("Teste autor").dataNascimento(new Date()).build();
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(50L).idUsuario(usuarioAtualizar.getId()).isbn("002").titulo("Teste Livro atualizar")
						.subtitulo("Teste subtitulo atualizar").sinopse("Teste sinopse atualizar").build();
		
		// Executando o buscar do livro
		Long idNaoEncontrado = 50L;
		when(livroService.getDetalhe(idNaoEncontrado,LIVRO_MSG)).thenThrow(new EntidadeNotFoundException("Livro não encontrado."));

		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisição do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_LIVROS + "/{idLivro}",criarLivroDTO.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Livro não encontrado.")));
		
	}
	
	@Test
	@DisplayName("Deve buscar livros com parâmetros de paginação e o título do livro.")
	public void deveBuscarLivrosPaginacao() throws Exception{
		// Cenário
		UsuarioDTO usuario = UsuarioDTO.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		List<LivroDTO> livroDTOs = new ArrayList<>();
		
		LivroDTO livroDTO = LivroDTO.builder().id(50L).usuarioDto(usuario).isbn("002").titulo("Teste Livro 1")
				.subtitulo("Teste subtitulo 11").sinopse("Teste sinopse 4444").build();
		LivroDTO livroDTO2 = LivroDTO.builder().id(50L).usuarioDto(usuario).isbn("003").titulo("Teste Livro 4")
				.subtitulo("Teste subtitulo 22").sinopse("Teste sinopse 36543534").build();
		LivroDTO livroDTO3 = LivroDTO.builder().id(50L).usuarioDto(usuario).isbn("004").titulo("Teste Livro 5")
				.subtitulo("Teste subtitulo 44").sinopse("Teste sinopse 34534543").build();
		LivroDTO livroDTO4 = LivroDTO.builder().id(50L).usuarioDto(usuario).isbn("005").titulo("Teste Livro 6")
				.subtitulo("Teste subtitulo 66").sinopse("Teste sinopse 4634534").build();
		LivroDTO livroDTO5 = LivroDTO.builder().id(50L).usuarioDto(usuario).isbn("006").titulo("Teste Livro 8")
				.subtitulo("Teste subtitulo 88").sinopse("Teste sinopse 11233").build();
		
		livroDTOs.add(livroDTO);
		livroDTOs.add(livroDTO2);
		livroDTOs.add(livroDTO3);
		livroDTOs.add(livroDTO4);
		livroDTOs.add(livroDTO5);
		
		// Executando o método 
		Pageable pageable = PageRequest.of(0, 2);
		when(livroService.buscarLivrosPaginadas("Teste",pageable)).thenReturn(new PageImpl<>(livroDTOs, pageable, 2));
		
		// Query da paginação -> /api/livros?pagina=0&totalRegistro=2
		String queryPagincao = API_LIVROS + "?titulo=Teste&pagina=0&totalRegistro=2";
		
		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryPagincao).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("content", Matchers.hasSize(5))) // Total de registros
				.andExpect(jsonPath("totalElements").value(2)) // Total de conteúdo
				.andExpect(jsonPath("pageable.pageSize").value(2)) // Tamanho
				.andExpect(jsonPath("pageable.pageNumber").value(0)); // Número página
	}
	
}
