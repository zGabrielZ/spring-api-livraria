package com.gabrielferreira.br.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Categoria;
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.dto.criar.CriarCategoriaDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.CategoriaDTO;
import com.gabrielferreira.br.service.CategoriaService;
import com.gabrielferreira.br.service.LivroService;

@SpringBootTest
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
@AutoConfigureMockMvc // Configuração do teste para configurar os objetos
public class CategoriaControllerTest {

	private static String API_CATEGORIAS = "/api/categorias";
	private static MediaType JSON_MEDIATYPE = MediaType.APPLICATION_JSON;
	
	@Autowired
	private MockMvc mockMvc; // Mockando as requisições
	
	@MockBean // Quando o contexto subir, não pode injetar o objeto real, so com os objetos falsos
	private CategoriaService categoriaService;
	
	@MockBean // Quando o contexto subir, não pode injetar o objeto real, so com os objetos falsos
	private LivroService livroService;
	
	@Test
	@DisplayName("Deve inserir a categoria utilizando a requisição POST com sucesso.")
	public void deveInserirCategoria() throws Exception{
		// Cenário 
		CriarCategoriaDTO criarCategoriaDTO = CriarCategoriaDTO.builder().id(null).descricao("Aventuras").build();
		
		// Criar entidade já com o id mockado
		Categoria categoriaCriado = Categoria.builder().id(1L).descricao(criarCategoriaDTO.getDescricao()).build();
		when(categoriaService.inserirCategoria(any())).thenReturn(categoriaCriado);
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarCategoriaDTO);
		
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_CATEGORIAS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
				
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").value(categoriaCriado.getId()))
				.andExpect(jsonPath("descricao").value(categoriaCriado.getDescricao()));
	}
	
	@Test
	@DisplayName("Não deve inserir categoria pois não tem dados suficiente.")
	public void naoDeveInserirCategoria() throws Exception{
		// Cenário 
		CriarCategoriaDTO criarCategoriaDTO = CriarCategoriaDTO.builder().id(null).descricao(null).build();
					
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarCategoriaDTO);
				
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_CATEGORIAS).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
						
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("campos", Matchers.hasSize(1)));
	}
	
	@Test
	@DisplayName("Deve atualizar categoria utilizando a requisição PUT com sucesso.")
	public void deveAtualizarCategoria() throws Exception{
		// Cenário
		
		// Criar entidade que já está salva no banco de dados 
		Categoria categoriaJaSalva = Categoria.builder().id(2L).descricao("Ação").build();
		
		// Criando a nossa categoria que quer atualizar 
		CriarCategoriaDTO criarCategoriaDTO = CriarCategoriaDTO.builder().id(2L)
				.descricao("Aventuras").build();
		
		// Criando a nossa categoria que já foi feito o update
		Categoria categoriaAtualizado = Categoria.builder().id(criarCategoriaDTO.getId()).descricao(criarCategoriaDTO.getDescricao()).build();
		
		// Executando o buscar da categoria
		when(categoriaService.getDetalhe(categoriaJaSalva.getId())).thenReturn(categoriaJaSalva);
		
		// Executando o atualizar da categoria
		when(categoriaService.inserirCategoria(any())).thenReturn(categoriaAtualizado);
				
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarCategoriaDTO);
				
		// Criar uma requisição do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_CATEGORIAS + "/{idCategoria}",criarCategoriaDTO.getId())
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("id").value(categoriaAtualizado.getId()))
				.andExpect(jsonPath("descricao").value(categoriaAtualizado.getDescricao()));					
	}
	
	@Test
	@DisplayName("Não deve atualizar categoria, pois não encontrou o id da categoria na qual quer atualizar.")
	public void naoDeveAtualizarCategoria() throws Exception{
		// Cenário
		// Criando a nossa categoria para fazer o update 
		CriarCategoriaDTO criarCategoriaDTO = CriarCategoriaDTO.builder().id(100L)
				.descricao("Aventuras").build();
		
		// Executando o buscar da categoria
		when(categoriaService.getDetalhe(criarCategoriaDTO.getId())).thenThrow(new EntidadeNotFoundException("Categoria não encontrado."));
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarCategoriaDTO);
				
		// Criar uma requisição do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_CATEGORIAS + "/{idCategoria}",criarCategoriaDTO.getId())
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("mensagem", equalTo("Categoria não encontrado.")));
	}
	
	@Test
	@DisplayName("Deve deletar categoria utilizando a requisição DELETE com sucesso.")
	public void deveDeletarCategoria() throws Exception{
		// Cenário 
		Categoria categoria = Categoria.builder().id(2l).descricao("Terror").build();
				
		// Executando o detalhe da categoria
		when(categoriaService.getDetalhe(categoria.getId())).thenReturn(categoria);
		
		// Executando a lista vazia de livros associados ao categoria
		when(livroService.livrosPorCategoriaId(anyLong())).thenReturn(new ArrayList<>());
				
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_CATEGORIAS + "/{idCategoria}",categoria.getId())
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Não deve deletar categoria, pois não encontrou o id da categoria na qual quer deletar.")
	public void naoDeveDeletarCategoria() throws Exception{
		// Cenário 
		Long idCategoria = 150L;
				
		// Executando o detalhe da categoria
		when(categoriaService.getDetalhe(idCategoria)).thenThrow(new EntidadeNotFoundException("Categoria não encontrado."));
				
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_CATEGORIAS + "/{idCategoria}",idCategoria)
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
						
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("mensagem", equalTo("Categoria não encontrado.")));
	}
	
	@Test
	@DisplayName("Não deve deletar categoria, pois tem livros associados nesta categoria.")
	public void naoDeveDeletarCategoriaLivros() throws Exception{
		// Cenário 
		Categoria categoria = Categoria.builder().id(2l).descricao("Terror").build();
						
		// Executando o detalhe da categoria
		when(categoriaService.getDetalhe(categoria.getId())).thenReturn(categoria);
				
		// Executando a lista de livros associados ao categoria
		List<Livro> livros = new ArrayList<Livro>();
		livros.add(Livro.builder().id(1L).titulo("Teste").subtitulo("Subtitulo").sinopse("Sinopse").isbn("123321")
				.usuario(null).categoria(categoria).build());
		when(livroService.livrosPorCategoriaId(categoria.getId())).thenThrow(new 
				RegraDeNegocioException("Não é possível deletar categoria pois tem livros associados !"));
						
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_CATEGORIAS + "/{idCategoria}",categoria.getId())
						.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
								
		// Fazendo o teste e verificando
		mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("mensagem", equalTo("Não é possível deletar categoria pois tem livros associados !")));
	}
	
	@Test
	@DisplayName("Deve obter informação de categoria utilizando a requisição GET com sucesso.")
	public void deveObterInformacaoCategoria() throws Exception{
		// Cenário 
		Categoria categoria = Categoria.builder().id(2L).descricao("Teste categoria").build();
	
		// Executando o buscar da categoria
		when(categoriaService.getDetalhe(categoria.getId())).thenReturn(categoria);
				
		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_CATEGORIAS + "/{idCategoria}",categoria.getId())
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("id").value(categoria.getId()))
					.andExpect(jsonPath("descricao").value(categoria.getDescricao()));
	}
	
	@Test
	@DisplayName("Não deve obter informação da categoria, pois não encontrou o id da categoria.")
	public void naoDeveObterInformacaoCategoria() throws Exception{
		// Cenário
		Long idCategoria = 122L;

		// Executando o buscar da categoria
		when(categoriaService.getDetalhe(idCategoria)).thenThrow(new EntidadeNotFoundException("Categoria não encontrado."));

		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_CATEGORIAS + "/{idCategoria}",idCategoria)
				.accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("mensagem", equalTo("Categoria não encontrado.")));
	}
	
	@Test
	@DisplayName("Deve obter lista de categorias utilizando a requisição GET com sucesso (Paginação).")
	public void deveMostrarListaDeCategorias() throws Exception{
		// Cenário
		List<CategoriaDTO> categoriaDTOs = new ArrayList<CategoriaDTO>();
		categoriaDTOs.add(CategoriaDTO.builder().id(1L).descricao("Ação").build());
		categoriaDTOs.add(CategoriaDTO.builder().id(2L).descricao("Aventuras").build());
		categoriaDTOs.add(CategoriaDTO.builder().id(3L).descricao("Terror").build());
				
		// Executando o buscar das categorias filtro com o mock de cima
		when(categoriaService.mostrarCategorias()).thenReturn(categoriaDTOs);

		// Criar uma requisição do tipo get
		String queryPaginacao = API_CATEGORIAS + "?pagina="+0+"&totalRegistro="+2;

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
	@DisplayName("Não deve obter lista de categorias (Paginação), pois não encontrou nenhum registro.")
	public void naoDeveMostrarListaDeCategorias() throws Exception{
		// Cenário
				
		// Executando o buscar das categorias
		when(categoriaService.mostrarCategorias()).thenThrow(new EntidadeNotFoundException("Nenhuma categoria encontrada."));

		// Criar uma requisição do tipo get
		String queryPaginacao = API_CATEGORIAS + "?pagina="+0+"&totalRegistro="+2;

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryPaginacao).accept(JSON_MEDIATYPE)
								.contentType(JSON_MEDIATYPE);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("mensagem", equalTo("Nenhuma categoria encontrada.")));
	}
}
