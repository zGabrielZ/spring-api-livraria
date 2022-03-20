package com.gabrielferreira.br.service;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.repositorio.UsuarioRepositorio;
import com.gabrielferreira.br.service.abstrato.AbstractService;

@Service
public class UsuarioService extends AbstractService<Usuario>{
	
	private final UsuarioRepositorio usuarioRepositorio;
	
	public UsuarioService(JpaRepository<Usuario, Long> jpaRepository) {
		super(jpaRepository);
		this.usuarioRepositorio = (UsuarioRepositorio) jpaRepository;
	}
	
	@Transactional
	public Usuario inserir(CriarUsuarioDTO criarUsuarioDTO) {
		Usuario usuario = new Usuario(criarUsuarioDTO.getId(), criarUsuarioDTO.getAutor(), criarUsuarioDTO.getDataNascimento(), null);
		verificarAutorExistente(usuario);
		return usuarioRepositorio.save(usuario);
	}
	
	public void verificarAutorExistente(Usuario usuario) {
		if(usuario.getId() == null) {
			
			Boolean existeAutor = usuarioRepositorio.existsByAutor(usuario.getAutor());
			if(existeAutor) {
				throw new RegraDeNegocioException("Este autor já foi cadastrado.");
			}
		
		} else if(usuario.getId() != null) {
			
			Usuario usuarioPesquisado = usuarioRepositorio.buscarAutorUsuarioQuandoForAtualizar(usuario.getAutor(), usuario.getId());
			if(usuarioPesquisado != null) {
				throw new RegraDeNegocioException("Autor já existente ao atualizar.");
			}
			
		}
	}
}
