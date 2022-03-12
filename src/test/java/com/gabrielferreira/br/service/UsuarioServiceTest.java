package com.gabrielferreira.br.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Date;
import java.util.Optional;

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
import com.gabrielferreira.br.repositorio.UsuarioRepositorio;

@ExtendWith(SpringExtension.class) // O Spring deve rodar um mini contexto de injeção de dependecia para rodar os testes
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
public class UsuarioServiceTest {
	
	private UsuarioService usuarioService;
	
	private UsuarioRepositorio usuarioRepositorio;
	
	private CriarUsuarioDTO criarUsuarioDTO;
	
	@BeforeEach
	public void criarInstancias() {
		usuarioRepositorio = Mockito.mock(UsuarioRepositorio.class);
		usuarioService = new UsuarioService(usuarioRepositorio);
		
		criarUsuarioDTO = CriarUsuarioDTO.builder().id(null).autor("José da Silva").dataNascimento(new Date()).build();
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
	@DisplayName("Não deve inserir usuário pois o autor já foi cadastrado anteriomente.")
	public void naoDeveInserirUsuarioAutorRepetido() {
		// Cenário já foi construido no criarInstancias()
		
		// Quando verificar qualquer coisa no verificar autor existete, retorna como true
		when(usuarioRepositorio.existsByAutor(criarUsuarioDTO.getAutor())).thenReturn(true);
		
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
		Usuario usuarioExistente = usuarioService.getUsuario(anyLong());
		
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
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class, () -> usuarioService.getUsuario(anyLong()));
		
		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage(exception.getMessage());
		verify(usuarioRepositorio).findById(anyLong());
	}
}
