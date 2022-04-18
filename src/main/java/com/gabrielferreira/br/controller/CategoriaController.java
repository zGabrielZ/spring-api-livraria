package com.gabrielferreira.br.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Categoria;
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.dto.criar.CriarCategoriaDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.CategoriaDTO;
import com.gabrielferreira.br.service.CategoriaService;
import com.gabrielferreira.br.service.LivroService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/categorias")
@Api("Categoria API")
public class CategoriaController {

	private static String CATEGORIA_MSG = "Categoria";
	
	@Autowired
	private CategoriaService categoriaService;
	
	@Autowired
	private LivroService livroService;
	
	@PostMapping
	@ApiOperation("Inserir uma categoria")
	@ApiResponses(value = {
			@ApiResponse(code = 201,message = "Inseriu a categoria com sucesso"),
			@ApiResponse(code = 400,message = "Ocorreu um erro personalizado"),
			@ApiResponse(code = 401,message = "Não autorizado para inserir a categoria"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado a categoria"),
	})
	public ResponseEntity<CriarCategoriaDTO> criarCategoria(@Valid @RequestBody CriarCategoriaDTO criarCategoriaDTO){
		Categoria categoria = categoriaService.inserirCategoria(criarCategoriaDTO);
		CriarCategoriaDTO categoriaDTO = new CriarCategoriaDTO(categoria);
		return new ResponseEntity<>(categoriaDTO,HttpStatus.CREATED);
	}
	
	@PutMapping("/{idCategoria}")
	@ApiOperation("Atualizar uma categoria informando o ID")
	@ApiResponses(value = {
			@ApiResponse(code = 201,message = "Atualizou a categoria com sucesso"),
			@ApiResponse(code = 401,message = "Não autorizado para atualizar a categoria"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado a categoria"),
	})
	public ResponseEntity<CriarCategoriaDTO> atualizarLivro(@PathVariable Long idCategoria, @RequestBody @Valid CriarCategoriaDTO categoriaDto){
		Categoria categoria = categoriaService.getDetalhe(idCategoria,CATEGORIA_MSG);
		categoriaDto.setId(categoria.getId());
		categoria = categoriaService.inserirCategoria(categoriaDto);
		CriarCategoriaDTO criarCategoriaDto = new CriarCategoriaDTO(categoria);
		return new ResponseEntity<>(criarCategoriaDto,HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/{idCategoria}")
	@ApiOperation("Deletar uma categoria por ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200,message = "Retornou os valores com sucesso"),
			@ApiResponse(code = 204,message = "Categoria deletada com sucesso"),
			@ApiResponse(code = 401,message = "Não autorizado para deletar a categoria"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado a categoria"),
	})
	public ResponseEntity<Void> deletarCategoria(@PathVariable Long idCategoria){
		Categoria categoria = categoriaService.getDetalhe(idCategoria, CATEGORIA_MSG);
		
		List<Livro> livros = livroService.livrosPorCategoriaId(categoria.getId());
		if(!livros.isEmpty()) {
			throw new RegraDeNegocioException("Não é possível deletar categoria pois tem livros associados !");
		}
		
		categoriaService.deletar(categoria.getId(), CATEGORIA_MSG);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{idCategoria}")
	@ApiOperation("Obtém informação de uma categoria por ID")
	@ApiResponses(value = {
			@ApiResponse(code = 401,message = "Não autorizado para consultar a categoria"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado a categoria"),
	})
	public ResponseEntity<CategoriaDTO> obterInformacaoCategoria(@PathVariable Long idCategoria){
		Categoria categoria = categoriaService.getDetalhe(idCategoria, CATEGORIA_MSG);
		CategoriaDTO categoriaDTO = new CategoriaDTO(categoria);
		return new ResponseEntity<>(categoriaDTO,HttpStatus.OK);
	}
	
	@GetMapping
	@ApiOperation("Paginação da listagem de categorias")
	@ApiResponses(value = {
			@ApiResponse(code = 401,message = "Não autorizado para consultar as categorias"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado a categoria"),
	})
	public ResponseEntity<PagedListHolder<CategoriaDTO>> mostrarTodasCategoriasPaginada(
			@RequestParam(defaultValue = "0", value = "pagina") int pagina,
			@RequestParam(defaultValue = "5", value = "totalRegistro") int totalRegistro
			){
		List<CategoriaDTO> categoriaDTOs = categoriaService.mostrarCategorias();
		
		PagedListHolder<CategoriaDTO> pagedListHolder = new PagedListHolder<CategoriaDTO>(categoriaDTOs);
		pagedListHolder.setPage(pagina);
		pagedListHolder.setPageSize(totalRegistro);
		
		return new ResponseEntity<>(pagedListHolder,HttpStatus.OK);
	}
}
