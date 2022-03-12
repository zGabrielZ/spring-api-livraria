package com.gabrielferreira.br.controller;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;

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
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.service.UsuarioService;

@SpringBootTest
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
@AutoConfigureMockMvc // Configuração do teste para configurar os objetos
public class UsuarioControllerTest {

	private static String API_USUARIO = "/api/usuarios";
	private static MediaType JSON_MEDIATYPE = MediaType.APPLICATION_JSON;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private MockMvc mockMvc; // Mockando as requisições
	
	@MockBean // Quando o contexto subir, não pode injetar o objeto real, so com os objetos falsos
	private UsuarioService usuarioService;
	
	@Test
	@DisplayName("Deve criar o usuário utilizando a requisição do tipo POST com sucesso.")
	public void deveInserirUsuario() throws Exception {
		
		// Cenário
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(null).autor("José Pereira da Silva").dataNascimento(sdf.parse("14/07/1965")).build();
		
		// Criar a entidade com o id já mockado
		Usuario usuarioCriado = Usuario.builder().id(1L).autor(criarUsuarioDTO.getAutor()).dataNascimento(criarUsuarioDTO.getDataNascimento()).build();
		
		// Executando o inserir do usuário
		when(usuarioService.inserir(any())).thenReturn(usuarioCriado);
		
		// Transformar o objto em json
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(criarUsuarioDTO);
		
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_USUARIO).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		// Verificar a requisição
		mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").value(usuarioCriado.getId()))
			.andExpect(jsonPath("autor").value(usuarioCriado.getAutor()))
			.andExpect(jsonPath("dataNascimento").value(sdf.format(usuarioCriado.getDataNascimento())));
	}
	
	@Test
	@DisplayName("Não deve criar usuário, pois não tem dados suficientes (Nome e data de nascimento não informados).")
	public void naoDeveInserirUsuarioNaoTemDadoSuficiente() throws Exception {
		
		// Cenário
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(null).autor(null).dataNascimento(null).build();
		
		// Transformar o objto em json
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(criarUsuarioDTO);
				
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_USUARIO).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		// Verificar a requisição
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("campos",Matchers.hasSize(2)));
				
	}
	
	@Test
	@DisplayName("Não deve criar usuário, pois o nome não está entre a faixa de 5 a 150 caracteres e também a data de nascimento é depois do que a data atual.")
	public void naoDeveInserirUsuarioTemDataNascimentoEnomeUsuario() throws Exception {
		
		// Cenário
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(null).autor("Abc").dataNascimento(sdf.parse("26/03/2022")).build();
		
		// Transformar o objto em json
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(criarUsuarioDTO);
				
		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_USUARIO).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		// Verificar a requisição
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("campos",Matchers.hasSize(2)));
				
	}
	
	@Test
	@DisplayName("Não deve inserir o usuário, pois já tem o autor já cadastrado por outro.")
	public void naoDeveInserirUsuarioPoisTemAutorCadastrado() throws Exception {
		
		// Cenário
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(1L).autor("José Pereira da Silva").dataNascimento(sdf.parse("14/07/1965")).build();

		// Executando o inserir do livro
		when(usuarioService.inserir(any()))
				.thenThrow(new RegraDeNegocioException("Este autor já foi cadastrado."));

		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarUsuarioDTO);

		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_USUARIO).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE).content(json);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Este autor já foi cadastrado.")));
	}
	
}
