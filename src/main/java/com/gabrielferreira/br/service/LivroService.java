package com.gabrielferreira.br.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarLivroDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.LivroDTO;
import com.gabrielferreira.br.repositorio.LivroRepositorio;
import com.gabrielferreira.br.service.abstrato.AbstractService;
import com.gabrielferreira.br.utils.ValidacaoFormatacao;

@Service
public class LivroService extends AbstractService<Livro>{
	
	private static String USUARIO_MSG = "Usuário";
	
	private final LivroRepositorio livroRepositorio;
	
	private final UsuarioService usuarioService;
	
	public LivroService(JpaRepository<Livro, Long> jpaRepository, UsuarioService usuarioService) {
		super(jpaRepository);
		livroRepositorio = (LivroRepositorio) jpaRepository;
		this.usuarioService = usuarioService;
	}
	
	@Transactional
	public Livro inserir(CriarLivroDTO criarLivroDTO) {
		Usuario usuario = usuarioService.getDetalhe(criarLivroDTO.getIdUsuario(),USUARIO_MSG);
		Livro livro = new Livro(criarLivroDTO.getId(), ValidacaoFormatacao.getFormatacaoNome(criarLivroDTO.getTitulo()), criarLivroDTO.getSubtitulo(), criarLivroDTO.getSinopse(), criarLivroDTO.getIsbn(), usuario);
		verificarTituloExistente(livro);
		verificarIsbnExistente(livro);
		ValidacaoFormatacao.getVerificarIsbn(livro.getIsbn());
		return livroRepositorio.save(livro);
	}
	
	public Page<LivroDTO> buscarLivrosPaginadas(String titulo,Pageable pageable){
		Page<Livro> pageLivro = livroRepositorio.buscarPorTituloPaginada(titulo, pageable);
		Page<LivroDTO> pageLivroDto = pageLivro.map(l -> new LivroDTO(l));
		return pageLivroDto;
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
