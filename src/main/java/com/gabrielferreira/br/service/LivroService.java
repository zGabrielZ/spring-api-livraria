package com.gabrielferreira.br.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarLivroDTO;
import com.gabrielferreira.br.repositorio.LivroRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivroService {
	
	private final LivroRepositorio livroRepositorio;
	
	private final UsuarioService usuarioService;
	
	@Transactional
	public Livro inserir(CriarLivroDTO criarLivroDTO) {
		Usuario usuario = usuarioService.getUsuario(criarLivroDTO.getIdUsuario());
		Livro livro = new Livro(criarLivroDTO.getId(), criarLivroDTO.getTitulo(), criarLivroDTO.getSubtitulo(), criarLivroDTO.getSinopse(), criarLivroDTO.getIsbn(), usuario);
		verificarTituloExistente(criarLivroDTO.getTitulo());
		verificarIsbnExistente(livro.getIsbn());
		return livroRepositorio.save(livro);
	}
	
	public Livro getLivro(Long id) {
		Optional<Livro> optionalLivro = livroRepositorio.findById(id);
		if(!optionalLivro.isPresent()) {
			throw new EntidadeNotFoundException("Livro não encontrado.");
		}
		return optionalLivro.get();
	}
	
	public void verificarTituloExistente(String titulo) {
		Boolean existeLivro = livroRepositorio.existsByTitulo(titulo);
		if(existeLivro) {
			throw new RegraDeNegocioException("Este Título já foi cadastrado por outro livro.");
		}
	}
	
	public void verificarIsbnExistente(String isbn) {
		Livro livro = livroRepositorio.buscarIsbnLivro(isbn);
		if(livro != null) {
			throw new RegraDeNegocioException("Este ISBN já foi cadastrado por outro livro.");
		}
	}
}
