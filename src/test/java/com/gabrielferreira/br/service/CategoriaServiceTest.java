package com.gabrielferreira.br.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.modelo.Categoria;
import com.gabrielferreira.br.modelo.dto.criar.CriarCategoriaDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.CategoriaDTO;
import com.gabrielferreira.br.repositorio.CategoriaRepositorio;

@ExtendWith(SpringExtension.class) // O Spring deve rodar um mini contexto de injeção de dependecia para rodar os testes
@ActiveProfiles("test") // Rodar com o perfil de teste, rodar com o ambiente de teste
public class CategoriaServiceTest {

	private CategoriaRepositorio categoriaRepositorio;
	private CategoriaService categoriaService;
	
	@BeforeEach
	public void criarInstancias() {
		categoriaRepositorio = Mockito.mock(CategoriaRepositorio.class);
		categoriaService = new CategoriaService(categoriaRepositorio);
	}
	
	@Test
	@DisplayName("Deve inserir a categoria com todas as informações")
	public void deveInserirCategoria() {
		// Cenário 
		// Informações inseridas
		CriarCategoriaDTO criarCategoriaDTO = CriarCategoriaDTO.builder().id(null).descricao("Aventuras").build();
		
		// Categoria com mock 
		Categoria categoriaCriado = Categoria.builder().id(2L).descricao(criarCategoriaDTO.getDescricao()).build();
		when(categoriaRepositorio.save(any(Categoria.class))).thenReturn(categoriaCriado);
		
		// Executando o método
		Categoria categoriaSalvo = categoriaService.inserirCategoria(criarCategoriaDTO);
		
		// Verificando se foi invocado
		verify(categoriaRepositorio).save(any(Categoria.class));
		
		// Verificando 
		assertThat(categoriaSalvo).isNotNull();
		assertThat(categoriaSalvo.getDescricao()).isEqualTo(criarCategoriaDTO.getDescricao());
		
	}
	
	@Test
	@DisplayName("Deve atualizar categoria com as descrição")
	public void deveAtualizarCategoria() {
		// Cenário
		// Informações inseridas
		CriarCategoriaDTO criarCategoriaDTO = CriarCategoriaDTO.builder().id(2L).descricao("Terror").build();
		
		// Categoria com mock update
		Categoria categoriaCriado = Categoria.builder().id(criarCategoriaDTO.getId()).descricao(criarCategoriaDTO.getDescricao()).build();
		when(categoriaRepositorio.save(any(Categoria.class))).thenReturn(categoriaCriado);
		
		// Executando o método
		Categoria categoriaAtualizar = categoriaService.inserirCategoria(criarCategoriaDTO);
		
		// Verificando se foi invocado
		verify(categoriaRepositorio).save(any(Categoria.class));
		
		// Verificando 
		assertThat(categoriaAtualizar.getId()).isEqualTo(criarCategoriaDTO.getId());
		assertThat(categoriaAtualizar.getDescricao()).isEqualTo(criarCategoriaDTO.getDescricao());
		
	}
	
	@Test
	@DisplayName("Deve mostrar todas as categorias.")
	public void deveMostrarCategorias() {
		// Cenário
		List<Categoria> categoriasMock = new ArrayList<Categoria>();
		categoriasMock.add(Categoria.builder().id(2L).descricao("Aventuras").build());
		categoriasMock.add(Categoria.builder().id(3L).descricao("Terror").build());
		categoriasMock.add(Categoria.builder().id(4L).descricao("Ação").build());
		categoriasMock.add(Categoria.builder().id(5L).descricao("Ficção").build());
		
		// Mock das categorias 
		when(categoriaRepositorio.findAll()).thenReturn(categoriasMock);
		
		// Executando método
		List<CategoriaDTO> categorias = categoriaService.mostrarCategorias();
		
		// Verificando 
		assertThat(!categorias.isEmpty()).isTrue();
		assertThat(categorias.size()).isEqualTo(4);
	}
	
	@Test
	@DisplayName("Não deve mostrar categorias.")
	public void naoDeveMostrarCategorias() {
		// Cenário 	
		when(categoriaRepositorio.findAll()).thenReturn(new ArrayList<>());
				
		// Executando 
		Throwable exception = Assertions.assertThrows(EntidadeNotFoundException.class, () -> categoriaService.mostrarCategorias());
				
		// Verificação
		assertThat(exception).isInstanceOf(EntidadeNotFoundException.class).hasMessage(exception.getMessage());
		verify(categoriaRepositorio).findAll();
	}
	
}
