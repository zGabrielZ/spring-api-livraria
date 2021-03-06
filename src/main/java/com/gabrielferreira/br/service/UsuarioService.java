package com.gabrielferreira.br.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.UsuarioDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarUsuarioDTO;
import com.gabrielferreira.br.repositorio.UsuarioRepositorio;
import com.gabrielferreira.br.service.abstrato.AbstractService;
import com.gabrielferreira.br.utils.ValidacaoFormatacao;

import org.apache.commons.lang3.StringUtils;

@Service
public class UsuarioService extends AbstractService<Usuario>{
	
	private final UsuarioRepositorio usuarioRepositorio;
	
	private final EntityManager entityManager;
	
	public UsuarioService(JpaRepository<Usuario, Long> jpaRepository, EntityManager entityManager) {
		super(jpaRepository);
		this.usuarioRepositorio = (UsuarioRepositorio) jpaRepository;
		this.entityManager = entityManager;	
	}
	
	@Transactional
	public Usuario inserir(CriarUsuarioDTO criarUsuarioDTO) {
		Usuario usuario = new Usuario(criarUsuarioDTO.getId(), ValidacaoFormatacao.getFormatacaoNome(criarUsuarioDTO.getAutor()), criarUsuarioDTO.getDataNascimento(), null);
		verificarAutorExistente(usuario);
		return usuarioRepositorio.save(usuario);
	}
	
	public List<UsuarioDTO> filtroUsuarios(ProcurarUsuarioDTO procurarUsuarioDTO){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
		Root<Usuario> root = cq.from(Usuario.class);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		if(procurarUsuarioDTO.getAutor() != null || StringUtils.isNotEmpty(procurarUsuarioDTO.getAutor())) {
			Predicate predicateNome = cb.like(root.get("autor"), "%" + procurarUsuarioDTO.getAutor() + "%");
			predicates.add(predicateNome);
		}
		
		if(procurarUsuarioDTO.getDataNascimentoInicio() == null && procurarUsuarioDTO.getDataNascimentoFinal() != null) {
			Predicate predicateDataNascimentoMenorIgual = cb.lessThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.getDataNascimentoFinal());
			predicates.add(predicateDataNascimentoMenorIgual);
		}
		
		if(procurarUsuarioDTO.getDataNascimentoInicio() != null && procurarUsuarioDTO.getDataNascimentoFinal() == null) {
			LocalDate dataNascimento = procurarUsuarioDTO.setParaLocalDate(procurarUsuarioDTO.getDataNascimentoInicio()).minusDays(1);
			Predicate predicateDataNascimentoMaiorIgual = cb.greaterThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.setParaDate(dataNascimento));
			predicates.add(predicateDataNascimentoMaiorIgual);
		}
		
		if(procurarUsuarioDTO.getDataNascimentoInicio() != null && procurarUsuarioDTO.getDataNascimentoFinal() != null) {
			
			if(procurarUsuarioDTO.getDataNascimentoInicio().after(procurarUsuarioDTO.getDataNascimentoFinal())){
				throw new RegraDeNegocioException("Data de nascimento in??cio n??o pode ser maior do que a data nascimento final.");
			}
			
			LocalDate dataNascimento = procurarUsuarioDTO.setParaLocalDate(procurarUsuarioDTO.getDataNascimentoInicio()).minusDays(1);
			Predicate predicateDataNascimentoMaiorIgual = cb.greaterThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.setParaDate(dataNascimento));
			Predicate predicateDataNascimentoMenorIgual = cb.lessThanOrEqualTo(root.get("dataNascimento"), procurarUsuarioDTO.getDataNascimentoFinal());
			Predicate predicateDataNascimento = cb.and(predicateDataNascimentoMaiorIgual,predicateDataNascimentoMenorIgual);
			predicates.add(predicateDataNascimento);
		}
		
		cq.orderBy(cb.desc(root.get("dataNascimento")));
		cq.where((Predicate[])predicates.toArray(new Predicate[0]));
		
		TypedQuery<Usuario> typedQuery = entityManager.createQuery(cq);
		List<Usuario> usuarios = typedQuery.getResultList();
		
		if(usuarios.isEmpty()) {
			throw new EntidadeNotFoundException("Nenhum usu??rio encontrado.");
		}
		
		List<UsuarioDTO> usuarioDTOs = usuarios.stream().map(u -> new UsuarioDTO(u)).collect(Collectors.toList());
		return usuarioDTOs;
	}
	
	public List<UsuarioDTO> mostrarUsuarios(){
		List<Usuario> usuarios = getLista();
		if(usuarios.isEmpty()) {
			throw new EntidadeNotFoundException("Nenhum usu??rio encontrado.");
		}
		List<UsuarioDTO> usuarioDTOs = usuarios.stream().map(u -> new UsuarioDTO(u)).collect(Collectors.toList());
		return usuarioDTOs;
	}
	
	public void verificarAutorExistente(Usuario usuario) {
		if(usuario.getId() == null) {
			
			Boolean existeAutor = usuarioRepositorio.existsByAutor(usuario.getAutor());
			if(existeAutor) {
				throw new RegraDeNegocioException("Este autor j?? foi cadastrado.");
			}
		
		} else if(usuario.getId() != null) {
			
			Usuario usuarioPesquisado = usuarioRepositorio.buscarAutorUsuarioQuandoForAtualizar(usuario.getAutor(), usuario.getId());
			if(usuarioPesquisado != null) {
				throw new RegraDeNegocioException("Autor j?? existente ao atualizar.");
			}
			
		}
	}
}
