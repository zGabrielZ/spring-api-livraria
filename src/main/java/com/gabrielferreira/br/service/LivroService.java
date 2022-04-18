package com.gabrielferreira.br.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Categoria;
import com.gabrielferreira.br.modelo.Livro;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarLivroDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.LivroDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarLivroDTO;
import com.gabrielferreira.br.repositorio.LivroRepositorio;
import com.gabrielferreira.br.service.abstrato.AbstractService;
import com.gabrielferreira.br.utils.ValidacaoFormatacao;

@Service
public class LivroService extends AbstractService<Livro>{
	
	private final LivroRepositorio livroRepositorio;
	
	private final UsuarioService usuarioService;
	
	private final CategoriaService categoriaService;
	
	private final EntityManager entityManager;
	
	public LivroService(JpaRepository<Livro, Long> jpaRepository, UsuarioService usuarioService,CategoriaService categoriaService,EntityManager entityManager) {
		super(jpaRepository);
		this.livroRepositorio = (LivroRepositorio) jpaRepository;
		this.usuarioService = usuarioService;
		this.categoriaService = categoriaService;
		this.entityManager = entityManager;
	}
	
	@Transactional
	public Livro inserir(CriarLivroDTO criarLivroDTO) {
		Usuario usuario = usuarioService.getDetalhe(criarLivroDTO.getIdUsuario());
		Categoria categoria = categoriaService.getDetalhe(criarLivroDTO.getIdCategoria());
		
		Livro livro = new Livro(criarLivroDTO.getId(), ValidacaoFormatacao.getFormatacaoNome(criarLivroDTO.getTitulo()), 
				criarLivroDTO.getSubtitulo(), criarLivroDTO.getSinopse(), criarLivroDTO.getIsbn(),criarLivroDTO.getEstoque(),usuario,categoria);
		
		verificarTituloExistente(livro);
		verificarIsbnExistente(livro);
		ValidacaoFormatacao.getVerificarIsbn(livro.getIsbn());
		verificarEstoqueLivro(livro);
		
		return livroRepositorio.save(livro);
	}
	
	public List<LivroDTO> buscarLivrosPaginadas(ProcurarLivroDTO procurarLivroDTO){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Livro> cq = cb.createQuery(Livro.class);
		Root<Livro> root = cq.from(Livro.class);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		if(procurarLivroDTO.getTitulo() != null || StringUtils.isNotEmpty(procurarLivroDTO.getTitulo())) {
			Predicate predicateTitulo = cb.like(root.get("titulo"), "%" + procurarLivroDTO.getTitulo() + "%");
			predicates.add(predicateTitulo);
		}
		
		if(procurarLivroDTO.getIsbn() != null || StringUtils.isNotEmpty(procurarLivroDTO.getIsbn())) {
			Predicate predicateIsbn = cb.like(root.get("isbn"), "%" + procurarLivroDTO.getIsbn() + "%");
			predicates.add(predicateIsbn);
		}
		
		if(procurarLivroDTO.getUsuarioNome() != null || StringUtils.isNotEmpty(procurarLivroDTO.getUsuarioNome())) {
			Join<Livro, Usuario> usuarioJoin = root.join("usuario");
			usuarioJoin.alias("u");
			
			Predicate predicateNomeUsuario = cb.like(usuarioJoin.get("autor"), procurarLivroDTO.getUsuarioNome());
			predicates.add(predicateNomeUsuario);
		}
		
		if(procurarLivroDTO.getDescricaoCategoria() != null || StringUtils.isNotEmpty(procurarLivroDTO.getDescricaoCategoria())) {
			Join<Livro, Categoria> categoriaJoin = root.join("categoria");
			categoriaJoin.alias("c");
			
			Predicate predicateDescricaoCategoria = cb.like(categoriaJoin.get("descricao"), procurarLivroDTO.getDescricaoCategoria());
			predicates.add(predicateDescricaoCategoria);
		}
		
		cq.orderBy(cb.desc(root.get("titulo")));
		cq.where((Predicate[])predicates.toArray(new Predicate[0]));
		
		TypedQuery<Livro> typedQuery = entityManager.createQuery(cq);
		List<Livro> livros = typedQuery.getResultList();
		
		if(livros.isEmpty()) {
			throw new EntidadeNotFoundException("Nenhum livro encontrado.");
		}
		
		List<LivroDTO> livrosDtos = livros.stream().map(l -> new LivroDTO(l)).collect(Collectors.toList());
		return livrosDtos;
	}
	
	public List<Livro> livrosPorCategoriaId(Long idCategoria){
		List<Livro> livros = livroRepositorio.findLivrosByCategoriaId(idCategoria);
		return livros;
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
	
	public void verificarEstoqueLivro(Livro livro) {
		if(livro.getEstoque() < 0) {
			throw new RegraDeNegocioException("Estoque do livro não pode ser menor do que 0.");
		}
	}
}
