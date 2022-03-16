package com.gabrielferreira.br.repositorio;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.gabrielferreira.br.modelo.Usuario;

@ExtendWith(SpringExtension.class) // O Spring deve rodar um mini contexto de injeção de dependecia para rodar os testes
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
@DataJpaTest // Indicar que vai fazer teste com jpa, vai criar uma instancia no banco de dados em memoria e apenas testar e depois apagar essa instancia
public class UsuarioRepositorioTest {

	@Autowired // Criar cenários e simular um entityManager
	private TestEntityManager entityManager;
	
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	
	private Usuario usuario;
	
	@BeforeEach
	private void criarInstancias() {
		usuario = Usuario.builder().id(null).autor("Gabriel Ferreira").dataNascimento(new Date()).build();
	}
	
	@Test
	@DisplayName("Deve retornar como verdadeiro o autor pesquisado.")
	public void deveRetornarComoVerdadeiroOAutor() {
		
		// Cenário já foi construido no criarInstancias()
		
		// Persistindo na base do usuario
		entityManager.persist(usuario);
		
		// Buscando o nosso usuario 
		Boolean usuarioPesquisado = usuarioRepositorio.existsByAutor(usuario.getAutor());
	
		
		// Verificando
		assertThat(usuarioPesquisado).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar como falso o  o autor pesquisado.")
	public void deveRetornarFalseTitulo() {
		// Cenário já foi construido no criarInstancias()

		// Buscando o nosso usuario 
		Boolean usuarioPesquisado = usuarioRepositorio.existsByAutor(usuario.getAutor());
		
		// Verificação
		assertThat(usuarioPesquisado).isFalse();
	}
	
	@Test
	@DisplayName("Deve retornar usuário quando for buscar o autor na hora de atualizar.")
	public void deveRetornarUsuarioQuandoForBuscarAutorAtualizar() {
		
		// Cenário já foi construido no criarInstancias()
		
		// Persistindo na base do usuario
		entityManager.persist(usuario);
		
		Usuario usuario2 = Usuario.builder().id(null).autor("Teste").dataNascimento(new Date()).build();
		
		// Persistindo na base do usuario2
		entityManager.persist(usuario2);
		
		// Voce quer atualizar o usuario2, porém quer colocar o nome igual ao usuario (Gabriel Ferreira)
		usuario2.setAutor("Gabriel Ferreira");
		
		// Buscando o nosso usuario 
		Usuario usuarioPesquisado = usuarioRepositorio.buscarAutorUsuarioQuandoForAtualizar(usuario2.getAutor(),usuario2.getId());
	
		
		// Verificando
		assertThat(usuarioPesquisado).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar usuário nulo quando for buscar o autor na hora de atualizar.")
	public void deveRetornarUsuarioNuloQuandoForBuscarAutorAtualizar() {
		
		Usuario usuario2 = Usuario.builder().id(null).autor("Teste").dataNascimento(new Date()).build();
		
		// Buscando o nosso usuario 
		Usuario usuarioPesquisado = usuarioRepositorio.buscarAutorUsuarioQuandoForAtualizar(usuario2.getAutor(),usuario2.getId());
	
		
		// Verificando
		assertThat(usuarioPesquisado).isNull();
	}
	
}
