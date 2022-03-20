package com.gabrielferreira.br.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Optional;
import java.util.Date;

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
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarLivroDTO;
import com.gabrielferreira.br.repositorio.LivroRepositorio;

@ExtendWith(SpringExtension.class) // O Spring deve rodar um mini contexto de injeção de dependecia para rodar os testes
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
public class LivroServiceTest {
	
	private static String [] MENSAGENS = {"Usuário","Livro"};
	
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
		when(usuarioService.getDetalhe(criarLivroDTO.getIdUsuario(),MENSAGENS[0])).thenReturn(usuario);
		
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
	@DisplayName("Deve atualizar um livro com todas as informações preenchidas.")
	public void deveAtualizarLivro() throws ParseException {
		
		// Cénario
		// Criando o nosso livro para fazer o update 
		Usuario usuarioAtualizar = Usuario.builder().id(30L).autor("Teste autor").dataNascimento(new Date()).build();
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(50L).idUsuario(usuarioAtualizar.getId()).isbn("002").titulo("Teste Livro atualizar")
				.subtitulo("Teste subtitulo atualizar").sinopse("Teste sinopse atualizar").build();
		
		// Criar a entidade que já foi feito o update
		Livro livroAtualizado = Livro.builder().id(criarLivroDTO.getId()).usuario(usuarioAtualizar).isbn(criarLivroDTO.getIsbn()).titulo(criarLivroDTO.getTitulo())
							.subtitulo(criarLivroDTO.getSubtitulo()).sinopse(criarLivroDTO.getSinopse()).build();
		
		// Quando for verificar o método de isbn ja cadastrado, deve retornar null, ou seja não existe isbn repetido
		when(livroRepositorio.buscarIsbnLivroQuandoForAtualizar(criarLivroDTO.getIsbn(),criarLivroDTO.getId())).thenReturn(null);
		
		// Quando for verificar o titulo cadastrado, é pra retorna como false, ou seja não existe titulo cadastrado
		when(livroRepositorio.existsByTituloQuandoForAtualizar(criarLivroDTO.getTitulo(),criarLivroDTO.getId())).thenReturn(Optional.empty());
		
		// Quando for atualizar no repositorio, retornar o livro que queria já salvar
		when(livroRepositorio.save(any())).thenReturn(livroAtualizado);
		
		// Executando o método
		Livro livroParaAtualizar = livroService.inserir(criarLivroDTO);
		
		// Verificando se foi invocado o save no service
		verify(livroRepositorio).save(any());
		
		// Verificando 
		assertThat(livroParaAtualizar.getId()).isNotNull();
		assertThat(livroParaAtualizar.getIsbn()).isEqualTo(criarLivroDTO.getIsbn());
		assertThat(livroParaAtualizar.getTitulo()).isEqualTo(criarLivroDTO.getTitulo());
		assertThat(livroParaAtualizar.getSubtitulo()).isEqualTo(criarLivroDTO.getSubtitulo());
		assertThat(livroParaAtualizar.getSinopse()).isEqualTo(criarLivroDTO.getSinopse());
		
	}
	
	@Test
	@DisplayName("Não deve atualizar um livro, pois já tem o isbn cadastrado.")
	public void naoDeveAtualizarLivroIsbn() {
		// Cénario
		// Criando o nosso livro para fazer o update
		Usuario usuarioAtualizar = Usuario.builder().id(30L).autor("Teste autor").dataNascimento(new Date()).build();
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(50L).idUsuario(usuarioAtualizar.getId()).isbn("002")
				.titulo("Teste Livro atualizar").subtitulo("Teste subtitulo atualizar")
				.sinopse("Teste sinopse atualizar").build();

		// Entidade que já esta salva no banco do mock
		Livro livroSalvo = Livro.builder().id(50L).usuario(usuarioAtualizar).isbn("002")
				.titulo("Teste salvo").subtitulo("Teste subtitulo salvo")
				.sinopse("Teste sinopse salvo").build();

		// Quando for verificar o método de isbn ja cadastrado, deve retornar null, ou
		// seja não existe isbn repetido
		when(livroRepositorio.buscarIsbnLivroQuandoForAtualizar(criarLivroDTO.getIsbn(), criarLivroDTO.getId()))
				.thenReturn(livroSalvo);

		// Quando for verificar o titulo cadastrado, é pra retorna como false, ou seja
		// não existe titulo cadastrado
		when(livroRepositorio.existsByTituloQuandoForAtualizar(criarLivroDTO.getTitulo(), criarLivroDTO.getId()))
				.thenReturn(Optional.empty());


		// Execução
		Throwable exception = Assertions.assertThrows(RegraDeNegocioException.class, () -> livroService.inserir(criarLivroDTO));
				
		// Verificação
		assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage(exception.getMessage());
		verify(livroRepositorio,never()).save(any());
	}
	
	@Test
	@DisplayName("Não deve atualizar um livro, pois já tem o titulo cadastrado.")
	public void naoDeveAtualizarLivroTitulo() {
		// Cénario
		// Criando o nosso livro para fazer o update
		Usuario usuarioAtualizar = Usuario.builder().id(30L).autor("Teste autor").dataNascimento(new Date()).build();
		CriarLivroDTO criarLivroDTO = CriarLivroDTO.builder().id(50L).idUsuario(usuarioAtualizar.getId()).isbn("002")
				.titulo("Teste Livro atualizar").subtitulo("Teste subtitulo atualizar")
				.sinopse("Teste sinopse atualizar").build();

		// Entidade que já esta salva no banco do mock
		Livro livroSalvo = Livro.builder().id(50L).usuario(usuarioAtualizar).isbn("004")
				.titulo("Teste Livro atualizar").subtitulo("Teste subtitulo salvo")
				.sinopse("Teste sinopse salvo").build();

		// Quando for verificar o método de isbn ja cadastrado, deve retornar null, ou
		// seja não existe isbn repetido
		when(livroRepositorio.buscarIsbnLivroQuandoForAtualizar(criarLivroDTO.getIsbn(), criarLivroDTO.getId()))
				.thenReturn(null);

		// Quando for verificar o titulo cadastrado, é pra retorna como false, ou seja
		// não existe titulo cadastrado
		when(livroRepositorio.existsByTituloQuandoForAtualizar(criarLivroDTO.getTitulo(), criarLivroDTO.getId()))
				.thenReturn(Optional.of(livroSalvo));


		// Execução
		Throwable exception = Assertions.assertThrows(RegraDeNegocioException.class, () -> livroService.inserir(criarLivroDTO));
				
		// Verificação
		assertThat(exception).isInstanceOf(RegraDeNegocioException.class).hasMessage(exception.getMessage());
		verify(livroRepositorio,never()).save(any());
	}
	
	@Test
	@DisplayName("Não deve inserir um livro, pois já tem o isbn cadastrado.")
	public void naoDeveInserirLivroIsbn() {
		//Cenário
		
		// Usuário já está salvo no banco do mockado
		Usuario usuario = Usuario.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		// Quando for buscar o usuario, é pra retorna o de cima
		when(usuarioService.getDetalhe(usuario.getId(),MENSAGENS[0])).thenReturn(usuario);
		
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
		when(usuarioService.getDetalhe(usuario.getId(),MENSAGENS[0])).thenReturn(usuario);
		
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
	
	@Test
	@DisplayName("Deve buscar o livro com o id informado.")
	public void deveBuscarLivro() {
		// Cenário 
		
		Usuario usuario = Usuario.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		
		Livro livro = Livro.builder().id(22L).usuario(usuario).isbn("Teste isbn").titulo("Teste titulo")
				.subtitulo("Teste subtitulo").sinopse("Teste sinopse").build();
		
		// Quando for buscar o livro, retornar o livro de cima 
		doReturn(Optional.of(livro)).when(livroRepositorio).findById(livro.getId());
		
		// Executando o método de buscar
		Livro livroExistente = livroService.getDetalhe(livro.getId(),MENSAGENS[1]);
		
		// Verificando se foi invocado o find by
		verify(livroRepositorio).findById(anyLong());
		
		// Verificando se foi igual
		assertThat(livroExistente.getId()).isNotNull();
		assertThat(livroExistente.getUsuario().getId()).isNotNull();
		assertThat(livroExistente.getIsbn()).isEqualTo(livro.getIsbn());
		assertThat(livroExistente.getTitulo()).isEqualTo(livro.getTitulo());
		assertThat(livroExistente.getSubtitulo()).isEqualTo(livro.getSubtitulo());
		assertThat(livroExistente.getSinopse()).isEqualTo(livro.getSinopse());	
	}
	
	@Test
	@DisplayName("Deve lançar uma exception, pois não encontrou o livro.")
	public void naoDeveBuscarLivro() {
		// Cenário 	
		when(livroRepositorio.findById(anyLong())).thenReturn(Optional.empty());
		
		// Executando 
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class, () -> livroService.getDetalhe(anyLong(),MENSAGENS[1]));
		
		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage(exception.getMessage());
		verify(livroRepositorio).findById(anyLong());
	}
	
	@Test
	@DisplayName("Deve deletar o livro pelo id informado.")
	public void deveDeletarLivro() {

		// Cenário
		Usuario usuario = Usuario.builder().id(1L).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		
		Livro livro = Livro.builder().id(22L).usuario(usuario).isbn("Teste isbn").titulo("Teste titulo")
				.subtitulo("Teste subtitulo").sinopse("Teste sinopse").build();
		
		// Quando for buscar o livro, vai ter que retornar o livro de cima 
		doReturn(Optional.of(livro)).when(livroRepositorio).findById(livro.getId());
		
		// Executand o método 
		livroService.deletar(livro.getId(),MENSAGENS[1]);
		
		// Verificação
		verify(livroRepositorio).deleteById(livro.getId());
		
	}
	
	@Test
	@DisplayName("Não deve deletar livro pois o id não foi informado.")
	public void naoDeveDeletarLivro() {
		
		// Cenário
		Livro livro = new Livro();
		
		// Executando 
		Assertions.assertThrows(IllegalArgumentException.class, () -> livroService.deletar(livro.getId(),MENSAGENS[1]));
		
		// Verificando
		verify(livroRepositorio,never()).delete(livro);
	}
}
