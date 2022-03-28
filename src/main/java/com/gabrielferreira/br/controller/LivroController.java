package com.gabrielferreira.br.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.dto.criar.CriarLivroDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.LivroDTO;
import com.gabrielferreira.br.service.LivroService;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

	private static String LIVRO_MSG = "Livro";
	
	@Autowired
	private LivroService livroService;
	
	@PostMapping
	public ResponseEntity<CriarLivroDTO> criarLivro(@RequestBody @Valid CriarLivroDTO livroDto){
		Livro livro	= livroService.inserir(livroDto);
		CriarLivroDTO criarLivroDTO = new CriarLivroDTO(livro);
		return new ResponseEntity<>(criarLivroDTO,HttpStatus.CREATED);
	}
	
	@GetMapping("/{idLivro}")
	public ResponseEntity<LivroDTO> obterInformacaoLivro(@PathVariable Long idLivro){
		Livro livro = livroService.getDetalhe(idLivro,LIVRO_MSG);
		LivroDTO livroDTO = new LivroDTO(livro);
		return new ResponseEntity<>(livroDTO,HttpStatus.OK);
	}
	
	@DeleteMapping("/{idLivro}")
	public ResponseEntity<Void> deletarLivro(@PathVariable Long idLivro){
		Livro livro = livroService.getDetalhe(idLivro,LIVRO_MSG);
		livroService.deletar(livro.getId(),LIVRO_MSG);
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/{idLivro}")
	public ResponseEntity<CriarLivroDTO> atualizarLivro(@PathVariable Long idLivro, @RequestBody @Valid CriarLivroDTO livroDto){
		Livro livro = livroService.getDetalhe(idLivro,LIVRO_MSG);
		livroDto.setId(livro.getId());
		livro = livroService.inserir(livroDto);
		CriarLivroDTO criarLivroDTO = new CriarLivroDTO(livro);
		return new ResponseEntity<>(criarLivroDTO,HttpStatus.NO_CONTENT);
	}
	
	@GetMapping
	public ResponseEntity<Page<LivroDTO>> buscarLivroPaginada(
			@RequestParam(required = false) String titulo,
			@RequestParam(value = "pagina",defaultValue = "0") int pagina,
			@RequestParam(value = "totalRegistro", defaultValue = "3") int totalRegistro){
		Pageable pageable = PageRequest.of(pagina, totalRegistro);
		Page<LivroDTO> page = livroService.buscarLivrosPaginadas(titulo,pageable);
		return new ResponseEntity<>(page,HttpStatus.OK);
	}
}
