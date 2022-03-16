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
		verificarTituloExistente(livro);
		verificarIsbnExistente(livro);
		return livroRepositorio.save(livro);
	}
	
	public void deletarLivro(Long id) {
		if(id == null) {
			throw new IllegalArgumentException("Para deletar o livro é preciso informar o id.");
		}
		livroRepositorio.deleteById(id);
	}
	
	public Livro getLivro(Long id) {
		Optional<Livro> optionalLivro = livroRepositorio.findById(id);
		if(!optionalLivro.isPresent()) {
			throw new EntidadeNotFoundException("Livro não encontrado.");
		}
		return optionalLivro.get();
	}
	
	public void verificarTituloExistente(Livro livro) {
		
		if(livro.getId() == null) {
			
			Boolean existeLivro = livroRepositorio.existsByTitulo(livro.getTitulo());
			if(existeLivro) {
				throw new RegraDeNegocioException("Este Título já foi cadastrado por outro livro.");
			}
		
		} else if(livro.getId() != null) {
			Optional<Livro> existeLivroAtualizar = livroRepositorio.existsByTituloQuandoForAtualizar(livro.getTitulo(),livro.getId());
			if(existeLivroAtualizar.isPresent()) {
				throw new RegraDeNegocioException("Título já existente ao atualizar.");
			}
		}
		
	}
	
	public void verificarIsbnExistente(Livro livro) {
		
		
		if(livro.getId() == null) {
			
			Livro livroExistente = livroRepositorio.buscarIsbnLivro(livro.getIsbn());
			
			if(livroExistente != null) {
				throw new RegraDeNegocioException("Este ISBN já foi cadastrado por outro livro.");
			}
			
		} else if(livro.getId() != null) {
			
			Livro livroExistenteAtualizar = livroRepositorio.buscarIsbnLivroQuandoForAtualizar(livro.getIsbn(),livro.getId());
			
			if(livroExistenteAtualizar != null) {
				throw new RegraDeNegocioException("ISBN já existente ao atualizar.");
			}
		}
	}
}
