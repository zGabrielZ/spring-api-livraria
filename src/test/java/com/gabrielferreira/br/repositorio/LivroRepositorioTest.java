package com.gabrielferreira.br.repositorio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.Usuario;

@ExtendWith(SpringExtension.class) // O Spring deve rodar um mini contexto de injeção de dependecia para rodar os testes
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
@DataJpaTest // Indicar que vai fazer teste com jpa, vai criar uma instancia no banco de dados em memoria e apenas testar e depois apagar essa instancia
public class LivroRepositorioTest {

	@Autowired // Criar cenários e simular um entityManager
	private TestEntityManager entityManager;
	
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	
	@Autowired
	private LivroRepositorio livroRepositorio;
	
	private Livro livro;
	
	private Usuario usuario;
	
	@Test
	@DisplayName("Deve retornar o livro pesquisado com o isbn informado.")
	public void deveBuscarIsbnLivro() {
		
		// Cenário 
		usuario = Usuario.builder().id(null).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
		
		// Persistindo na base do usuario
		entityManager.persist(usuario);
		
		// Buscando o nosso usuario 
		Usuario usuarioPesquisado = usuarioRepositorio.findById(usuario.getId()).get();
		
		// Setando o nosso usuario com o livro
		livro = Livro.builder().id(null).usuario(usuarioPesquisado).isbn("001").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();
		
		// Persistindo na base do livro
		entityManager.persist(livro);
		
		// Executando
		Livro livroPesquisado = livroRepositorio.buscarIsbnLivro(livro.getIsbn());
		
		// Verificando
		assertNotNull(livroPesquisado);
	}
	
	@Test
	@DisplayName("Deve retornar nulo com o isbn informado.")
	public void deveRetornarComNullIsbn() {
		// Cenário 
		livro = Livro.builder().id(null).usuario(any()).isbn("001").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();
		
		// Executando
		Livro livroPesquisado = livroRepositorio.buscarIsbnLivro(livro.getIsbn());
		
		// Verificando
		assertThat(livroPesquisado).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar como verdadeiro o livro com titulo informado.")
	public void deveBuscarTituloLivro() {
		// Cenário
		usuario = Usuario.builder().id(null).autor("Gabriel Ferreira").dataNascimento(new Date()).build();

		// Persistindo na base do usuario
		entityManager.persist(usuario);

		// Buscando o nosso usuario
		Usuario usuarioPesquisado = usuarioRepositorio.findById(usuario.getId()).get();

		// Setando o nosso usuario com o livro
		livro = Livro.builder().id(null).usuario(usuarioPesquisado).isbn("001").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();

		// Persistindo na base do livro
		entityManager.persist(livro);

		// Execução
		Boolean existeLivro = livroRepositorio.existsByTitulo(livro.getTitulo());
		
		// Verificação
		assertThat(existeLivro).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar como falso o livro com titulo informado.")
	public void deveRetornarFalseTitulo() {
		// Cenário
		livro = Livro.builder().id(null).usuario(any()).isbn("001").titulo("Teste Livro").subtitulo("Teste teste").sinopse("Teste sinopse").build();

		// Execução
		Boolean existeLivro = livroRepositorio.existsByTitulo(livro.getTitulo());
		
		// Verificação
		assertThat(existeLivro).isFalse();
	}
	
}
