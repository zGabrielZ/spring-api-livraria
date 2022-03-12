package com.gabrielferreira.br.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarLivroDTO;
import com.gabrielferreira.br.repositorio.LivroRepositorio;

@ExtendWith(SpringExtension.class) // O Spring deve rodar um mini contexto de injeção de dependecia para rodar os testes
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
public class LivroServiceTest {
	
	private LivroService livroService;
	
	private UsuarioService usuarioService;
	
	private LivroRepositorio livroRepositorio;
	
	@BeforeEach
	public void criarInstancias() {
		usuarioService = Mockito.mock(UsuarioService.class);
		livroRepositorio = Mockito.mock(LivroRepositorio.class);
		livroService = new LivroService(livroRepositorio, usuarioService);
	}
	
	@Test
	@DisplayName("Deve inserir um livro com todas as informações preenchidas.")
	public void deveInserirLivro() throws ParseException {
		
		// Cenário
		Usuario usuario = Usuario.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuario.getId()).isbn("001").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();
		
		// Quando for buscar o usuario, vai retornar o usuario mockado 
		when(usuarioService.getUsuario(anyLong())).thenReturn(usuario);
		
		// Criar a entidade com o id já mockado
		Livro livroCriado = Livro.builder().id(22L).usuario(usuario).isbn(criarLivroDTO.getIsbn()).titulo(criarLivroDTO.getTitulo())
				.subtitulo(criarLivroDTO.getSubtitulo()).sinopse(criarLivroDTO.getSinopse()).build();
		
		// Quando for verificar o método de isbn ja cadastrado, deve retornar null, ou seja não existe isbn repetido
		when(livroRepositorio.buscarIsbnLivro(criarLivroDTO.getIsbn())).thenReturn(null);
		
		// Quando for verificar o titulo cadastrado, é pra retorna como false, ou seja não existe titulo cadastrado
		when(livroRepositorio.existsByTitulo(criarLivroDTO.getTitulo())).thenReturn(false);
		
		// Quando for salvar no repositorio, retornar o livro criado ja com o id embutido
		when(livroRepositorio.save(any())).thenReturn(livroCriado);
		
		// Executando o método
		Livro livroSaldo = livroService.inserir(criarLivroDTO);
		
		// Verificando se foi invocado o save no service
		verify(livroRepositorio).save(any());
		
		// Verificando 
		assertThat(livroSaldo.getId()).isNotNull();
		assertThat(livroSaldo.getIsbn()).isEqualTo(criarLivroDTO.getIsbn());
		assertThat(livroSaldo.getTitulo()).isEqualTo(criarLivroDTO.getTitulo());
		assertThat(livroSaldo.getSubtitulo()).isEqualTo(criarLivroDTO.getSubtitulo());
		assertThat(livroSaldo.getSinopse()).isEqualTo(criarLivroDTO.getSinopse());
		
	}
	
	@Test
	@DisplayName("Não deve inserir um livro, pois já tem o isbn cadastrado.")
	public void naoDeveInserirLivroIsbn() {
		//Cenário
		
		// Usuário já está salvo no banco do mockado
		Usuario usuario = Usuario.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		// Quando for buscar o usuario, é pra retorna o de cima
		when(usuarioService.getUsuario(usuario.getId())).thenReturn(usuario);
		
		// Fazendo o nosso criar livro
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuario.getId()).isbn("001").titulo("Teste Livro Gabriel").subtitulo("Teste teste Gabriel")
				.sinopse("Teste sinopse gabriel").build();
		
		// Já está salvo no banco mockado e com o isbn igual da variavel criarLivroDto
		Livro livroBuscado = Livro.builder().id(22L).usuario(any()).isbn("001").titulo("Teste Livro 123").subtitulo("Teste teste 123").sinopse("Teste sinopse 123").build();
		// Quando for verificar o isbn cadastrado, é pra retorna com o livro de cima
		when(livroRepositorio.buscarIsbnLivro(criarLivroDTO.getIsbn())).thenReturn(livroBuscado);
		// Quando for verificar o titulo cadastrado, é pra retorna como false, ou seja não existe titulo cadastrado
		when(livroRepositorio.existsByTitulo(criarLivroDTO.getTitulo())).thenReturn(false);
		
		// Execução
		Throwable exception = Assertions.assertThrows(RegraDeNegocioException.class, () -> livroService.inserir(criarLivroDTO));
		
		// Verificação
		assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage(exception.getMessage());
		verify(livroRepositorio,never()).save(any());
	}
	
	@Test
	@DisplayName("Não deve inserir um livro, pois já tem o titulo cadastrado.")
	public void naoDeveInserirLivroTitulo() {
		//Cenário
		
		// Usuário já está salvo no banco do mockado
		Usuario usuario = Usuario.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		// Quando for buscar o usuario, é pra retorna o de cima
		when(usuarioService.getUsuario(usuario.getId())).thenReturn(usuario);
		
		// Fazendo o nosso criar livro
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(null).idUsuario(usuario.getId()).isbn("001").titulo("Teste Livro Gabriel").subtitulo("Teste teste Gabriel")
				.sinopse("Teste sinopse gabriel").build();
		
		// Quando for verificar o titulo cadastrado, é pra retorna como true, ou seja existe o titulo cadastrado
		when(livroRepositorio.existsByTitulo(criarLivroDTO.getTitulo())).thenReturn(true);
		
		// Quando for verificar o isbn cadastrado, é pra retorna null, pois não existe
		when(livroRepositorio.buscarIsbnLivro(criarLivroDTO.getIsbn())).thenReturn(null);
		
		// Execução
		Throwable exception = Assertions.assertThrows(RegraDeNegocioException.class, () -> livroService.inserir(criarLivroDTO));
		
		// Verificação
		assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage(exception.getMessage());
		verify(livroRepositorio,never()).save(any());
	}
}
