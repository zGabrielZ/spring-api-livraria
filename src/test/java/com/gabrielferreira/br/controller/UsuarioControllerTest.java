package com.gabrielferreira.br.controller;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.UsuarioDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarUsuarioDTO;
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
	@DisplayName("Deve atualizar o usuário pelo id informado e campos encontrado.")
	public void deveAtualizarUsuario() throws Exception{
		
		// Cénario
		// Criar a entidade que já está salva no banco do mock
		Usuario usuarioJaSalvo = Usuario.builder().id(150L).autor("Gabriel Ferriera").dataNascimento(sdf.parse("14/07/1965")).build();
		
		// Criando o nosso usuário para fazer o update 
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(150L).autor("Teste 123").dataNascimento(sdf.parse("26/12/1997")).build();
		
		// Criar a entidade que já foi feito o update
		Usuario usuarioAtualizado = Usuario.builder().id(criarUsuarioDTO.getId()).autor(criarUsuarioDTO.getAutor()).dataNascimento(criarUsuarioDTO.getDataNascimento()).build();
		
		// Executando o buscar do usuário
		when(usuarioService.getDetalhe(usuarioJaSalvo.getId())).thenReturn(usuarioJaSalvo);
		
		// Executando o atualizar do usuário
		when(usuarioService.inserir(any())).thenReturn(usuarioAtualizado);
		
		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarUsuarioDTO);
		
		// Criar uma requisição do tipo put
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_USUARIO + "/{idUsuario}",criarUsuarioDTO.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE).content(json);
		
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
			.andDo(print())
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("id").value(usuarioAtualizado.getId()))
			.andExpect(jsonPath("autor").value(usuarioAtualizado.getAutor()))
			.andExpect(jsonPath("dataNascimento").value(sdf.format(usuarioAtualizado.getDataNascimento())));
		
	}
	
	@Test
	@DisplayName("Deve deletar usuário pelo id informado.")
	public void deveDeletarUsuario() throws Exception {
		
		// Cenário 
		Usuario usuario = Usuario.builder().id(133L).autor("Teste usuário").dataNascimento(new Date()).build();
		
		// Executando o deletar do usuário
		when(usuarioService.getDetalhe(usuario.getId())).thenReturn(usuario);
		
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_USUARIO + "/{idUsuario}",usuario.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNoContent());
						
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
		LocalDate dataNascimento = LocalDate.now().plusDays(2);
		
		Date dataNascimentoFormat = Date.from(dataNascimento.atStartOfDay(ZoneId.systemDefault()).toInstant());
		
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(null).autor("Abc").dataNascimento(dataNascimentoFormat).build();
		
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

		// Executando o inserir do usuário
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
	
	@Test
	@DisplayName("Não deve atualizar o usuário, pois já tem o autor já cadastrado por outro.")
	public void naoDeveAtualizarUsuarioPoisTemAutorCadastrado() throws Exception {
		
		// Cenário
		CriarUsuarioDTO criarUsuarioDTO = CriarUsuarioDTO.builder().id(1L).autor("José Pereira da Silva").dataNascimento(sdf.parse("14/07/1965")).build();

		// Executando o inserir do usuário
		when(usuarioService.inserir(any()))
				.thenThrow(new RegraDeNegocioException("Autor já existente ao atualizar."));

		// Transformar o objeto em json
		String json = new ObjectMapper().writeValueAsString(criarUsuarioDTO);

		// Criar uma requisição do tipo post
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_USUARIO).accept(JSON_MEDIATYPE)
				.contentType(JSON_MEDIATYPE).content(json);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Autor já existente ao atualizar.")));
	}
	
	@Test
	@DisplayName("Deve buscar o usuário com o id informado.")
	public void deveBuscarUsuario() throws Exception {
		// Cenário 
		Usuario usuario = Usuario.builder().id(133L).autor("Teste usuário").dataNascimento(sdf.parse("12/12/1998")).build();
		
		// Executando o buscar do usuário
		when(usuarioService.getDetalhe(usuario.getId())).thenReturn(usuario);
		
		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_USUARIO + "/{idUsuario}",usuario.getId()).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(usuario.getId()))
				.andExpect(jsonPath("autor").value(usuario.getAutor()))
				.andExpect(jsonPath("dataNascimento").value(sdf.format(usuario.getDataNascimento())));
	}
	
	@Test
	@DisplayName("Não deve buscar o usuário, pois não encontrou informações dele.")
	public void naoDeveBuscarUsuario() throws Exception {
		
		// Cenário
		Long idUsuario = 122L;

		// Executando o buscar do usuário
		when(usuarioService.getDetalhe(idUsuario)).thenThrow(new EntidadeNotFoundException("Usuário não encontrado."));

		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_USUARIO + "/{idUsuario}",idUsuario).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Usuário não encontrado.")));
	}
	
	@Test
	@DisplayName("Não deve deletar livro pelo id informado pois não encontrou o livro.")
	public void naoDeveDeletarUsuario() throws Exception {
		// Cenário 
		Long idUsuario = 150L;
		
		// Executando o deletar do usuário
		when(usuarioService.getDetalhe(idUsuario)).thenThrow(new EntidadeNotFoundException("Usuário não encontrado."));
		
		// Criar uma requisição do tipo delete
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_USUARIO + "/{idUsuario}",idUsuario).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Usuário não encontrado.")));
						
	}
	
	@Test
	@DisplayName("Deve buscar o usuários cadastrados.")
	public void deveBuscarUsuarios() throws Exception {
		// Cenário 
		Usuario usuario1 = Usuario.builder().id(133L).autor("Teste usuário 0").dataNascimento(sdf.parse("12/12/1998")).build();
		Usuario usuario2 = Usuario.builder().id(134L).autor("Teste usuário 1").dataNascimento(sdf.parse("12/12/1998")).build();
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(usuario1);
		usuarios.add(usuario2);
		
		// Executando o buscar do usuário
		List<UsuarioDTO> usuarioDTOs = usuarios.stream().map(u -> new UsuarioDTO(u)).collect(Collectors.toList());
		when(usuarioService.mostrarUsuarios()).thenReturn(usuarioDTOs);
		
		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_USUARIO).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*]",Matchers.hasSize(2)));
	}
	
	@Test
	@DisplayName("Não deve buscar o usuários, pois nenhum foi cadastrado.")
	public void naoDeveBuscarUsuarios() throws Exception {
		
		// Cenário

		// Executando o buscar do usuário
		when(usuarioService.mostrarUsuarios()).thenThrow(new EntidadeNotFoundException("Nenhum usuário encontrado."));

		// Criar uma requisição do tipo get
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_USUARIO).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);

		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Nenhum usuário encontrado.")));
	}
	
	@Test
	@DisplayName("Deve mostrar uma lista de usuários com os parametros informados.")
	public void deveMostrarUsuariosFiltro() throws Exception{
		// Cenário
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ProcurarUsuarioDTO procurarUsuarioDTO = ProcurarUsuarioDTO.builder().autor("Gabriel")
				.dataNascimentoInicio(sdf.parse("04/04/2022")).dataNascimentoFinal(sdf.parse("05/04/2022")).build();

		// Fazendo a listagem de usuários
		List<UsuarioDTO> usuarios = new ArrayList<UsuarioDTO>();
		usuarios.add(UsuarioDTO.builder().id(1L).autor("Gabriel 1").dataNascimento(new Date()).build());
		usuarios.add(UsuarioDTO.builder().id(2L).autor("Gabriel 2").dataNascimento(new Date()).build());
		usuarios.add(UsuarioDTO.builder().id(3L).autor("Gabriel 3").dataNascimento(new Date()).build());
		
		// Executando o buscar do usuários filtro
		when(usuarioService.filtroUsuarios(any(ProcurarUsuarioDTO.class)))
				.thenReturn(usuarios);

		// Criar uma requisição do tipo get
		String queryPaginacao = API_USUARIO + "/filtro?autor=" + procurarUsuarioDTO.getAutor()
				+ "&dataNascimentoInicio=" + sdf.format(procurarUsuarioDTO.getDataNascimentoInicio())
				+ "&dataNascimentoFinal=" + sdf.format(procurarUsuarioDTO.getDataNascimentoFinal())
				+"&pagina="+0
				+"&totalRegistro="+2;

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
	@DisplayName("Não deve retornar usuários pois não tem usuários cadastrados.")
	public void naoDeveMostrarUsuariosSemUsuario() throws Exception{
		// Cenário 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ProcurarUsuarioDTO procurarUsuarioDTO = ProcurarUsuarioDTO.builder().autor("Gabriel").dataNascimentoInicio(sdf.parse("04/04/2022"))
				.dataNascimentoFinal(sdf.parse("05/04/2022"))
				.build();
		
		// Executando o buscar do usuários filtro
		when(usuarioService.filtroUsuarios(any(ProcurarUsuarioDTO.class))).thenThrow(new EntidadeNotFoundException("Nenhum usuário encontrado."));
		
		// Criar uma requisição do tipo get
		String queryPaginacao = API_USUARIO + "/filtro?autor="+procurarUsuarioDTO.getAutor()+"&dataNascimentoInicio="+sdf.format(procurarUsuarioDTO.getDataNascimentoInicio())
				+"&dataNascimentoFinal="+sdf.format(procurarUsuarioDTO.getDataNascimentoFinal());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryPaginacao).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
		
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("mensagem", equalTo("Nenhum usuário encontrado.")));
	}
	
	@Test
	@DisplayName("Não deve retornar usuários pois a data nascimento inicial é maior do que a data nascimento final.")
	public void naoDeveMostrarUsuariosParametroErrado() throws Exception{
		// Cenário 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ProcurarUsuarioDTO procurarUsuarioDTO = ProcurarUsuarioDTO.builder().autor("Gabriel").dataNascimentoInicio(sdf.parse("20/04/2022"))
					.dataNascimentoFinal(sdf.parse("05/04/2022"))
					.build();
				
		// Executando o buscar do usuários filtro
		when(usuarioService.filtroUsuarios(any(ProcurarUsuarioDTO.class))).thenThrow(new RegraDeNegocioException("Data de nascimento início não pode ser maior do que a data nascimento final."));
				
		// Criar uma requisição do tipo get
		String queryPaginacao = API_USUARIO + "/filtro?autor="+procurarUsuarioDTO.getAutor()+"&dataNascimentoInicio="+sdf.format(procurarUsuarioDTO.getDataNascimentoInicio())
				+"&dataNascimentoFinal="+sdf.format(procurarUsuarioDTO.getDataNascimentoFinal());
			
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryPaginacao).accept(JSON_MEDIATYPE).contentType(JSON_MEDIATYPE);
				
		// Fazendo o teste e verificando
		mockMvc.perform(request)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("mensagem", equalTo("Data de nascimento início não pode ser maior do que a data nascimento final.")));
	}
	
}
