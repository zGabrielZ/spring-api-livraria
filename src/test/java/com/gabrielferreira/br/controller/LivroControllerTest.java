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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Categoria;
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarLivroDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.LivroDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.UsuarioDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarLivroDTO;
import com.gabrielferreira.br.service.LivroService;

@SpringBootTest
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
@AutoConfigureMockMvc // ConfiguraĆ§Ć£o do teste para configurar os objetos
public class LivroControllerTest {

	private static String API_LIVROS = "/api/livros";
	private static MediaType JSON_MEDIATYPE = MediaType.APPLICATION_JSON;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private MockMvc mockMvc; // Mockando as requisiĆ§Ćµes
	
	@MockBean // Quando o contexto subir, nĆ£o pode injetar o objeto real, so com os objetos falsos
	private LivroService livroService;
	
	private Usuario usuarioCriado;
	private Categoria categoriaCriado;
	
	@BeforeEach
	public void criarInstancias() throws Exception{
		usuarioCriado = Usuario.builder().id(1L).autor("JosĆ© da Silva").dataNascimento(sdf.parse("26/12/1994")).build();
		categoriaCriado = Categoria.builder().id(2L).descricao("Aventuras").build();
	}
	
	@Test
	@DisplayName("Deve inserir o livro utilizando a requisiĆ§Ć£o POST com sucesso.")
	public void deveInserirLivro() throws Exception{
		
		// CĆ©nario
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001")
				.titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").estoque(100).idCategoria(categoriaCriado.getId()).build();
		
		// Criar a entidade com o id jĆ” mockado
		Livro livroCriado = Livro.builder().id(22L).usuario(usuarioCriado).isbn(criarLivroDTO.getIsbn()).titulo(criarLivroDTO.getTitulo())
				.subtitulo(criarLivroDTO.getSubtitulo()).sinopse(criarLivroDTO.getSinopse()).categoria(categoriaCriado).build();
		
		// Executando o inserir do livro
		when(livroService.inserir(any())).thenReturn(livroCriado);
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").value(livroCriado.getId()))
			.andExpect(jsonPath("titulo").value(livroCriado.getTitulo()))
			.andExpect(jsonPath("idUsuario").value(livroCriado.getUsuario().getId()))
			.andExpect(jsonPath("idCategoria").value(livroCriado.getCategoria().getId()))
			.andExpect(jsonPath("isbn").value(livroCriado.getIsbn()))
			.andExpect(jsonPath("subtitulo").value(livroCriado.getSubtitulo()))
			.andExpect(jsonPath("sinopse").value(livroCriado.getSinopse()))
			.andExpect(jsonPath("estoque").value(livroCriado.getEstoque()));
		
	}
	
	@Test
	@DisplayName("NĆ£o deve inserir o livro pois nĆ£o tem dados suficiente para criaĆ§Ć£o do livro.")
	public void naoDeveInserirLivroNaoTemDadoSuficiente() throws Exception{
		
		// CenĆ”rio
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(null).isbn(null).titulo(null).subtitulo(null).sinopse(null)
				.idCategoria(null).estoque(null).build();
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("campos", Matchers.hasSize(7)));
	
	}
	
	@Test
	@DisplayName("NĆ£o deve inserir o livro, pois jĆ” tem o isbn jĆ” cadastrado por outro.")
	public void naoDeveInserirLivroPoisTemIsbnCadastrado() throws Exception{
		
		// CenĆ”rio
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001").titulo("Teste Livro")
				.subtitulo("Teste teste").sinopse("Teste sinopse").estoque(100).idCategoria(categoriaCriado.getId()).build();
		
		// Executando o inserir do livro
		when(livroService.inserir(any())).thenThrow(new RegraDeNegocioException("Este ISBN jĆ” foi cadastrado por outro livro."));
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
				
		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Este ISBN jĆ” foi cadastrado por outro livro.")));
	}
	
	
	@Test
	@DisplayName("NĆ£o deve inserir o livro, pois jĆ” tem o tĆ­tulo jĆ” cadastrado por outro.")
	public void naoDeveInserirLivroPoisTemTituloCadastrado() throws Exception {
		
		// CenĆ”rio
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001")
				.titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").idCategoria(categoriaCriado.getId()).estoque(100).build();

		// Executando o inserir do livro
		when(livroService.inserir(any()))
				.thenThrow(new RegraDeNegocioException("Este TĆ­tulo jĆ” foi cadastrado por outro livro."));

		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);

		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE).content(json);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Este TĆ­tulo jĆ” foi cadastrado por outro livro.")));
	}
	
	@Test
	@DisplayName("NĆ£o deve inserir o livro, pois nĆ£o encontrou o usuĆ”rio informado.")
	public void naoDeveInserirLivroPoisNaoEncontrouUsuario() throws Exception {
		
		// CenĆ”rio
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001")
				.titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").idCategoria(categoriaCriado.getId()).estoque(100).build();

		// Executando o buscar do usuario
		when(livroService.inserir(any()))
				.thenThrow(new EntidadeNotFoundException("UsuĆ”rio nĆ£o encontrado."));

		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);

		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE).content(json);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("UsuĆ”rio nĆ£o encontrado.")));
	}
	
	@Test
	@DisplayName("NĆ£o deve inserir o livro, pois nĆ£o encontrou a categoria informada.")
	public void naoDeveInserirLivroPoisNaoEncontrouCategoria() throws Exception {
		
		// CenĆ”rio
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001")
				.titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").idCategoria(categoriaCriado.getId()).estoque(100).build();

		// Executando o buscar do usuario
		when(livroService.inserir(any()))
				.thenThrow(new EntidadeNotFoundException("Categoria nĆ£o encontrado."));

		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);

		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE).content(json);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Categoria nĆ£o encontrado.")));
	}
	
	@Test
	@DisplayName("NĆ£o deve inserir livro pois tem mais de 13 caracteres no campo ISBN.")
	public void naoDeveInserirLivroCaracteresIsbn() throws Exception{
		// CĆ©nario
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId())
				.isbn("0014309823480932840238048230").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").
				estoque(100).idCategoria(categoriaCriado.getId()).build();
	
		// Executando o verificar campo ISBN 
		when(livroService.inserir(any())).thenThrow(new RegraDeNegocioException("O limite de caracteres do ISBN Ć© atĆ© 13."));
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
						.contentType(JSON_MEDIATYPE).content(json);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("O limite de caracteres do ISBN Ć© atĆ© 13.")));
	}
	
	@Test
	@DisplayName("NĆ£o deve inserir livro pois o ISBN tem que ser numĆ©rico.")
	public void naoDeveInserirLivroNumericoIsbn() throws Exception{
		// CĆ©nario
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId())
					.isbn("asasaasdfdgdhrtghdhdrthrth").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").
					estoque(100).idCategoria(categoriaCriado.getId()).build();
			
		// Executando o verificar campo ISBN 
		when(livroService.inserir(any())).thenThrow(new RegraDeNegocioException("Ć necessĆ”rio inserir somente numĆ©rico para o ISBN."));
				
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
				
		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE)
						.contentType(JSON_MEDIATYPE).content(json);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("mensagem", equalTo("Ć necessĆ”rio inserir somente numĆ©rico para o ISBN.")));
	}
	
	@Test
	@DisplayName("NĆ£o deve inserir o livro, pois o estoque do livro nĆ£o pode ser menor do 0.")
	public void naoDeveInserirLivroEstoqueNegativo() throws Exception{
		
		// CenĆ”rio
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuarioCriado.getId()).isbn("001").titulo("Teste Livro")
				.subtitulo("Teste teste").sinopse("Teste sinopse").estoque(-100).idCategoria(categoriaCriado.getId()).build();
		
		// Executando o inserir do livro
		when(livroService.inserir(any())).thenThrow(new RegraDeNegocioException("Estoque do livro nĆ£o pode ser menor do que 0."));
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
				
		// Criar uma requisiĆ§Ć£o do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_LIVROS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Estoque do livro nĆ£o pode ser menor do que 0.")));
	}
	
	@Test
	@DisplayName("Deve buscar o livro com o id informado.")
	public void deveBuscarLivro() throws Exception {
		// CenĆ”rio 
		Usuario usuario = Usuario.builder().id(133L).autor("Teste usuĆ”rio").dataNascimento(new Date()).build();
		Categoria categoria = Categoria.builder().id(2L).descricao("Teste categoria").build();
		Livro livro = Livro.builder().id(12L).usuario(usuario).isbn("001").titulo("Teste Livro Gabriel").subtitulo("Teste teste Gabriel")
				.sinopse("Teste sinopse gabriel").categoria(categoria).build();
		
		// Executando o buscar do livro
		when(livroService.getDetalhe(livro.getId())).thenReturn(livro);
		
		// Criar uma requisiĆ§Ć£o do tipo get
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
	@DisplayName("NĆ£o deve buscar o livro, pois nĆ£o encontrou informaĆ§Ćµes dele.")
	public void naoDeveBuscarLivro() throws Exception {
		
		// CenĆ”rio
		Long idLivro = 122L;

		// Executando o buscar do livro
		when(livroService.getDetalhe(idLivro)).thenThrow(new EntidadeNotFoundException("Livro nĆ£o encontrado."));

		// Criar uma requisiĆ§Ć£o do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_LIVROS + "/{idLivro}",idLivro).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Livro nĆ£o encontrado.")));
	}
	
	@Test
	@DisplayName("Deve deletar livro pelo id informado.")
	public void deveDeletarLivro() throws Exception {
		// CenĆ”rio 
		Usuario usuario = Usuario.builder().id(133L).autor("Teste usuĆ”rio").dataNascimento(new Date()).build();
		Livro livro = Livro.builder().id(12L).usuario(usuario).isbn("001").titulo("Teste Livro Gabriel").subtitulo("Teste teste Gabriel")
				.sinopse("Teste sinopse gabriel").build();
		
		// Executando o deletar do livro
		when(livroService.getDetalhe(livro.getId())).thenReturn(livro);
		
		// Criar uma requisiĆ§Ć£o do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_LIVROS + "/{idLivro}",livro.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNoContent());
						
	}
	
	@Test
	@DisplayName("NĆ£o deve deletar livro pelo id informado pois nĆ£o encontrou o livro.")
	public void naoDeveDeletarLivro() throws Exception {
		// CenĆ”rio 
		Long idLivro = 150L;
		
		// Executando o deletar do livro
		when(livroService.getDetalhe(idLivro)).thenThrow(new EntidadeNotFoundException("Livro nĆ£o encontrado."));
		
		// Criar uma requisiĆ§Ć£o do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_LIVROS + "/{idLivro}",idLivro).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Livro nĆ£o encontrado.")));
						
	}
	
	@Test
	@DisplayName("Deve atualizar o livro pelo id informado e campos encontrado.")
	public void deveAtualizarLivro() throws Exception{
		
		// CĆ©nario
		
		// Criar a entidade que jĆ” estĆ” salva no banco do mock
		Livro livroJaSalvo = Livro.builder().id(50L).usuario(usuarioCriado).isbn("001").titulo("Teste teste")
					.subtitulo("Teste subtitulo").sinopse("Teste sinopse").estoque(100).build();
		
		// Criando o nosso livro para fazer o update 
		Usuario usuarioAtualizar = Usuario.builder().id(30L).autor("Teste autor").dataNascimento(new Date()).build();
		Categoria categoriaAtualizar = Categoria.builder().id(30L).descricao("DescriĆ§Ć£o categoria teste").build();
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(50L).idUsuario(usuarioAtualizar.getId()).isbn("002").titulo("Teste Livro atualizar")
				.subtitulo("Teste subtitulo atualizar").sinopse("Teste sinopse atualizar").estoque(200).idCategoria(categoriaAtualizar.getId()).build();
		
		// Criar a entidade que jĆ” foi feito o update
		Livro livroAtualizado = Livro.builder().id(criarLivroDTO.getId()).usuario(usuarioAtualizar).isbn(criarLivroDTO.getIsbn()).titulo(criarLivroDTO.getTitulo())
							.subtitulo(criarLivroDTO.getSubtitulo()).sinopse(criarLivroDTO.getSinopse()).categoria(categoriaAtualizar).build();
		
		// Executando o buscar do livro
		when(livroService.getDetalhe(livroJaSalvo.getId())).thenReturn(livroJaSalvo);
		
		// Executando o atualizar do livro
		when(livroService.inserir(any())).thenReturn(livroAtualizado);
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisiĆ§Ć£o do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_LIVROS + "/{idLivro}",criarLivroDTO.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("id").value(livroAtualizado.getId()))
			.andExpect(jsonPath("titulo").value(livroAtualizado.getTitulo()))
			.andExpect(jsonPath("idUsuario").value(livroAtualizado.getUsuario().getId()))
			.andExpect(jsonPath("idCategoria").value(livroAtualizado.getCategoria().getId()))
			.andExpect(jsonPath("isbn").value(livroAtualizado.getIsbn()))
			.andExpect(jsonPath("subtitulo").value(livroAtualizado.getSubtitulo()))
			.andExpect(jsonPath("sinopse").value(livroAtualizado.getSinopse()));
		
	}
	
	@Test
	@DisplayName("NĆ£o deve atualizar o livro pois nĆ£o encontrou o id como parametro.")
	public void naoDeveAtualizarLivroIdParametroNaoInformado() throws Exception{
		
		// CĆ©nario
		
		// Criando o nosso livro para fazer o update 
		Usuario usuarioAtualizar = Usuario.builder().id(30L).autor("Teste autor").dataNascimento(new Date()).build();
		Categoria categoriaAtualizar = Categoria.builder().id(30L).descricao("DescriĆ§Ć£o categoria teste").build();
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(50L).idUsuario(usuarioAtualizar.getId()).isbn("002").titulo("Teste Livro atualizar")
						.subtitulo("Teste subtitulo atualizar").sinopse("Teste sinopse atualizar").estoque(100).idCategoria(categoriaAtualizar.getId()).build();
		
		// Executando o buscar do livro
		Long idNaoEncontrado = 50L;
		when(livroService.getDetalhe(idNaoEncontrado)).thenThrow(new EntidadeNotFoundException("Livro nĆ£o encontrado."));

		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarLivroDTO);
		
		// Criar uma requisiĆ§Ć£o do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_LIVROS + "/{idLivro}",criarLivroDTO.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Livro nĆ£o encontrado.")));
		
	}
	
	// terminar os testes unitarios, fazer a documentaĆ§Ć£o, e continuar o video
	
	@Test
	@DisplayName("Deve buscar livros com parĆ¢metros de paginaĆ§Ć£o.")
	public void deveBuscarLivrosPaginacao() throws Exception{
		// CenĆ”rio
		ProcurarLivroDTO procurarLivroDTO = ProcurarLivroDTO.builder().titulo("Teste").isbn("123321").usuarioNome("Gab")
				.build();

		List<LivroDTO> livros = new ArrayList<LivroDTO>();
		livros.add(LivroDTO.builder().id(1L).titulo("Teste").subtitulo("Subtitulo").sinopse("Sinopse").isbn("123321")
				.usuarioDto(UsuarioDTO.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build())
				.build());
		
		// Executando o buscar do livros filtro com o mock de cima
		when(livroService.buscarLivrosPaginadas(any(ProcurarLivroDTO.class)))
				.thenReturn(livros);

		// Criar uma requisiĆ§Ć£o do tipo get
		String queryPaginacao = API_LIVROS + "/filtro?titulo=" + procurarLivroDTO.getTitulo() + "&isbn="
				+ procurarLivroDTO.getIsbn() + "&autor=" + procurarLivroDTO.getUsuarioNome()
				+"&pagina="+0
				+"&totalRegistro="+1;

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryPaginacao).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("source",Matchers.hasSize(1))) // Total de registros que foi buscado sem filtro
			.andExpect(jsonPath("pageSize").value(1)) // Quantidade de registro com o filtro
			.andExpect(jsonPath("pageList",Matchers.hasSize(1))) // Total de registro que foi buscado com filtro
			.andExpect(jsonPath("page").value(0)); // PĆ”gina que foi informado no parametro
			
	}
	
	@Test
	@DisplayName("NĆ£o deve buscar livros com parĆ¢metros de paginaĆ§Ć£o pois nĆ£o encontrou nenhum registro.")
	public void naoDeveBuscarLivrosPaginacao() throws Exception{
		// CenĆ”rio 
		ProcurarLivroDTO procurarLivroDTO = ProcurarLivroDTO.builder().titulo("Teste").isbn("123321").usuarioNome("Gab")
				.build();
						
		// Executando o buscar do livros filtro
		when(livroService.buscarLivrosPaginadas(any(ProcurarLivroDTO.class)))
			.thenThrow(new RegraDeNegocioException("Nenhum livro encontrado."));
						
		// Criar uma requisiĆ§Ć£o do tipo get
		String queryPaginacao = API_LIVROS + "/filtro?titulo="+procurarLivroDTO.getTitulo()+"&isbn="+procurarLivroDTO.getIsbn()
				+"&autor="+procurarLivroDTO.getUsuarioNome();
					
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryPaginacao).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Nenhum livro encontrado.")));
	}
	
}
