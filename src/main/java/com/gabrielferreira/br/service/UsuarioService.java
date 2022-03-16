package com.gabrielferreira.br.service;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.repositorio.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private final UsuarioRepositorio usuarioRepositorio;
	
	@Transactional
	public Usuario inserir(CriarUsuarioDTO criarUsuarioDTO) {
		Usuario usuario = new Usuario(criarUsuarioDTO.getId(), criarUsuarioDTO.getAutor(), criarUsuarioDTO.getDataNascimento(), null);
		verificarAutorExistente(usuario);
		return usuarioRepositorio.save(usuario);
	}
	
	@Transactional
	public void deletarUsuario(Long idUsuario) {
		usuarioRepositorio.deleteById(idUsuario);
	}
	
	public Usuario getUsuario(Long id) {
		Optional<Usuario> optionalUsuario = usuarioRepositorio.findById(id);
		if(!optionalUsuario.isPresent()) {
			throw new EntidadeNotFoundException("Usuário não encontrado.");
		}
		return optionalUsuario.get();
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
